package com.zero.itext.service;

import org.springframework.stereotype.Service;

@Service
public interface SignService {

    String uploadSign(String img);

    String sign();

}
