package com.zxw.reggie.dto;


import com.zxw.reggie.entity.Setmeal;
import com.zxw.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
