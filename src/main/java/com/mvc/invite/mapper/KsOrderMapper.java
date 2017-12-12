package com.mvc.invite.mapper;

import com.mvc.invite.model.KsOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by ywd-pc on 2017/12/11.
 */
public interface KsOrderMapper extends BaseMapper<KsOrder> {

    @Insert("insert into ks_order (name, cellphone, address, quantity, sum, invite_code, comment, status) values " +
            "(#{ksOrder.name},#{ksOrder.cellphone},#{ksOrder.address},#{ksOrder.quantity}," +
            " #{ksOrder.sum},#{ksOrder.inviteCode},#{ksOrder.comment},#{ksOrder.status})")
    int insertKsOrder(@Param("ksOrder") KsOrder ksOrder);

    @Select("select * from ks_order")
    List<KsOrder> selectKsOrders();

    @Select("select * from ks_order where cellphone=#{cellphone}")
    List<KsOrder> selectKsOrdersByPhone(@Param("cellphone") String cellphone);

    @Update("update ks_order set status=#{status}, comment=#{comment} " +
            "where id=#{id}")
    int updateComment(
            @Param("id") Integer id,
            @Param("status") Integer status,
            @Param("comment") String comment
    );

    @Update("update ks_order set status=#{status}" +
            "where id=#{id}")
    int updateStatus(
            @Param("id") Integer id,
            @Param("status") Integer status
    );
}