package com.example.bankapp.controller;

import com.example.bankapp.enums.AccountType;
import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.model.User;
import com.example.bankapp.service.AccountService;
import com.example.bankapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final AccountService accountService;
    private final UserService userService;


    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountId,
                          @RequestParam BigDecimal amount,
                          Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        Account account = accountService.getUserAccountById(user, accountId);

        accountService.deposit(account, amount);
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountId,
                           @RequestParam BigDecimal amount,
                           Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        try {
            Account account = accountService.getUserAccountById(user, accountId);
            accountService.withdraw(account, amount);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return getString(model, user);
        }
    }

    @PostMapping("/transfer")
    public String transferAmount(@RequestParam Long fromAccountId,
                                 @RequestParam String toUsername,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam(required = false) String description, // <-- NEW
                                 Principal principal,
                                 Model model) {
        try {
            // Fetch sender (already authenticated)
            User sender = userService.findByUsername(principal.getName());

            // Get sender's account
            Account fromAccount = accountService.getUserAccountById(sender, fromAccountId);

            // Find recipient
            User recipient = userService.findByUsername(toUsername);

            // Get recipient's CHECKING account
            Account toAccount = recipient.getAccounts().stream()
                    .filter(acc -> acc.getAccountType() == AccountType.CHECKING)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Recipient has no checking account"));

            // Perform the transfer
            accountService.transferAmount(fromAccount, toAccount, amount, description);

            return "redirect:/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());

            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
            return getString(model, user);
        }

    }

    private String getString(Model model, User user) {
        model.addAttribute("accounts", user.getAccounts());

        if (!user.getAccounts().isEmpty()) {
            model.addAttribute("account", user.getAccounts().get(0)); // Fixes "account.id" errors
        }

        List<User> otherUsers = userService.findAllExcept(user);
        model.addAttribute("otherUsers", otherUsers);

        return "dashboard";
    }

    @GetMapping("/transactions")
    public String transactionHistory(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        List<Transaction> allTransactions = user.getAccounts().stream()
                .flatMap(acc -> accountService.getTransactionHistory(acc).stream())
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();

        model.addAttribute("transactions", allTransactions);
        return "transactions";
    }

}
