package com.example.bankingapp.service.impl;

import com.example.bankingapp.dto.EmailOperation;
import com.example.bankingapp.exception.EmailHandlingException;
import com.example.bankingapp.exception.UserNotFoundException;
import com.example.bankingapp.model.EmailData;
import com.example.bankingapp.model.User;
import com.example.bankingapp.repository.EmailDataRepository;
import com.example.bankingapp.repository.UserRepository;
import com.example.bankingapp.service.EmailDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailDataServiceImpl implements EmailDataService {
    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;

    @Transactional
    public void handleEmailOperation(Long userId, EmailOperation operation) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (operation.getOperationType()) {
            case ADD -> addEmail(user, operation.getEmail());
            case UPDATE -> updateEmail(user, operation.getEmail(), operation.getEmailToChange());
            case REMOVE -> removeEmail(user, operation.getEmail());
        }
    }

    private void addEmail(User user, String email) {
        checkIfEmailExistsInDb(user, email);

        EmailData newEmail = EmailData.builder().user(user).email(email).build();
        user.getEmails().add(newEmail);
    }

    private void updateEmail(User user, String email, String emailToChange) {
        if (emailToChange == null) {
            throw new EmailHandlingException("Необходимо ввести email, который вы хотите заменить");
        }

        checkIfEmailExistsInDb(user, email);

        EmailData newEmailData = user.getEmails().stream()
                .filter(e -> e.getEmail().equals(emailToChange))
                .findFirst()
                .orElseThrow(() -> new EmailHandlingException(String.format("Email %s не зарегистрирован", emailToChange)));
        newEmailData.setEmail(email);
    }

    private void removeEmail(User user, String email) {
        boolean emailExists = user.getEmails()
                .stream()
                .map(EmailData::getEmail)
                .anyMatch(e -> e.equals(email));

        if (!emailExists) {
            throw new EmailHandlingException(String.format("Email %s не зарегистрирован", email));
        }

        if (user.getEmails().size() <= 1) {
            throw new EmailHandlingException("Вы не можете удалить последний email!");
        }

        user.getEmails().removeIf(e -> e.getEmail().equals(email));
    }

    private void checkIfEmailExistsInDb(User user, String email) {
        Optional<EmailData> existingEmail = emailDataRepository.findByEmail(email);

        if (existingEmail.isPresent()) {
            String message;
            Long idInDbByEmail = existingEmail.get().getUser().getId();

            if (Objects.equals(idInDbByEmail, user.getId())) {
                message = String.format("У вас уже зарегистрирован email %s", email);
            } else {
                message = String.format("У другого пользователя уже зарегистрирован email %s", email);
            }

            throw new EmailHandlingException(message);
        }
    }
}
