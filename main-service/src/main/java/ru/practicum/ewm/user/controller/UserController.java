package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.user.service.UserService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}