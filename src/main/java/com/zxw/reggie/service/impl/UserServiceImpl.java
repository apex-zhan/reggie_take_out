package com.zxw.reggie.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxw.reggie.entity.User;
import com.zxw.reggie.mapper.UserMapper;
import com.zxw.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    }
