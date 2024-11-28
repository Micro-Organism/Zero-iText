package com.zero.itext.controller;

import com.zero.itext.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sign")
public class SignController {

    private final SignService signService;
    @Autowired
    public SignController(SignService signService) {
        this.signService = signService;
    }

    @PostMapping(value = "/uploadSign")
    @ResponseBody
    public String uploadSign(String img) {
        return signService.uploadSign(img);
    }


    @PostMapping(value = "/sign")
    @ResponseBody
    public String sign() {
        return signService.sign();
    }
}
