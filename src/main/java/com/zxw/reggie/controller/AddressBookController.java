package com.zxw.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zxw.reggie.common.BaseContext;
import com.zxw.reggie.common.R;
import com.zxw.reggie.entity.AddressBook;
import com.zxw.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
//        通过BaseContext.getCurrentId()获取当前用户的ID，并将其设置为AddressBook对象的userId属性。这种做法使得AddressBook对象与当前用户保持同步，当用户ID发生变化时，AddressBook对象的userId属性会自动更新。
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
//        LambdaUpdateWrapper来构建一个Lambda表达式更新包装器
        LambdaUpdateWrapper<AddressBook> Wrapper = new LambdaUpdateWrapper<>();
//        这段代码的含义是让查询条件基于当前用户的ID。
        Wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
//        AddressBook 对象的 getIsDefault 方法返回值为 1。
        Wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(Wrapper);
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) return R.success(addressBook);
        else {
            return R.error("没有找到该对象");
        }

    }

    /**
     * 1. 查询默认地址
     */
    @GetMapping("/dafault")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
//        可以通过调用getOne方法来获取一个具体的地址簿对象，该对象包含了地址簿中的所有信息。这样，AddressBookController就可以对地址簿进行操作，比如查询、添加、修改和删除等。
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }
    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

}
















