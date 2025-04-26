package com.example.bankingapp.service.impl;

import com.example.bankingapp.dto.PhoneOperation;
import com.example.bankingapp.exception.PhoneHandlingException;
import com.example.bankingapp.exception.UserNotFoundException;
import com.example.bankingapp.model.PhoneData;
import com.example.bankingapp.model.User;
import com.example.bankingapp.repository.PhoneDataRepository;
import com.example.bankingapp.repository.UserRepository;
import com.example.bankingapp.service.PhoneDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneDataServiceImpl implements PhoneDataService {
    private final UserRepository userRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Transactional
    public void handlePhoneOperation(Long userId, PhoneOperation operation) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        switch (operation.getOperationType()) {
            case ADD -> addPhone(user, operation.getPhone());
            case UPDATE -> updatePhone(user, operation.getPhone(), operation.getPhoneToChange());
            case REMOVE -> removePhone(user, operation.getPhone());
        }
    }

    private void addPhone(User user, String phone) {
        checkIfPhoneExistsInDb(user, phone);

        PhoneData newPhone = PhoneData.builder().user(user).phone(phone).build();
        user.getPhones().add(newPhone);
    }

    private void updatePhone(User user, String phone, String phoneToChange) {
        if (phoneToChange == null) {
            throw new PhoneHandlingException("Необходимо ввести номер телефона, который вы хотите заменить");
        }

        checkIfPhoneExistsInDb(user, phone);

        PhoneData newPhoneData = user.getPhones().stream()
                .filter(f -> f.getPhone().equals(phoneToChange))
                .findFirst()
                .orElseThrow(() -> new PhoneHandlingException(String.format("Номер телефона %s не зарегистрирован", phoneToChange)));
        newPhoneData.setPhone(phone);
    }

    private void removePhone(User user, String phone) {
        boolean phoneExists = user.getPhones()
                .stream()
                .map(PhoneData::getPhone)
                .anyMatch(f -> f.equals(phone));

        if (!phoneExists) {
            throw new PhoneHandlingException(String.format("Номер телефона %s не зарегистрирован", phone));
        }

        if (user.getPhones().size() <= 1 ) {
            throw new PhoneHandlingException("Вы не можете удалить последний номер телефона!");
        }

        user.getPhones().removeIf(f -> f.getPhone().equals(phone));
    }

    private void checkIfPhoneExistsInDb(User user, String phone) {
        Optional<PhoneData> existingPhone = phoneDataRepository.findByPhone(phone);

        if (existingPhone.isPresent()) {
            String message;
            Long idInDbByPhone = existingPhone.get().getUser().getId();

            if (Objects.equals(idInDbByPhone, user.getId())) {
                message = String.format("У вас уже зарегистрирован номер телефона %s", phone);
            } else {
                message = String.format("У другого пользователя уже зарегистрирован номер телефона %s", phone);
            }

            throw new PhoneHandlingException(message);
        }
    }
}
