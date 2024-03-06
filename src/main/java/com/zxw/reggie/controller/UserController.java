package com.zxw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zxw.reggie.common.R;
import com.zxw.reggie.entity.User;
import com.zxw.reggie.service.UserService;
import com.zxw.reggie.utils.SMSUtils;
import com.zxw.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    // 自动注入UserService
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        //生成随机的4位
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code={}", code);
        //调用阿里云API发送短信
//        SMSUtils.sendMessage("1",phone,code);

        //将生成的验证码保存到Session
//        session.setAttribute(phone, code);
        //将我们生成的验证码缓存到redis中，并且设置时长为5分钟
        redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

        return R.success("手机验证码发送成功");


    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInRedis = redisTemplate.opsForValue().get(phone);


        //如果比对成功，返回登录成功，保存登录状态
        if (codeInRedis != null && codeInRedis.equals(code)) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号是否是新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                //save() 方法是用于将一个用户对象保存到数据库中。它是 User 类的一个方法，当创建一个新的 User 实例并将其添加到数据库中时，会调用这个方法。
                userService.save(user);

            }
            //把用户id存入session
            session.setAttribute("user", user.getId());

            //如果登入成功，删除redis中的验证码
            redisTemplate.delete(phone);


            return R.success(user);
        }

        return R.error("登入失败");
    }
}