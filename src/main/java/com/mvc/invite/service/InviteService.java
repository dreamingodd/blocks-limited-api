package com.mvc.invite.service;

import com.mvc.invite.mapper.InviteRecordMapper;
import com.mvc.invite.mapper.InviteUserMapper;
import com.mvc.invite.model.Auth;
import com.mvc.invite.model.InviteRecord;
import com.mvc.invite.model.InviteUser;
import com.mvc.invite.model.Order;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ywd-pc on 2017/12/8.
 */
@Log
@Service
public class InviteService {

    private static char[] inviteCodeChar = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static List<Auth> authList = new ArrayList<>();

    @Autowired
    private InviteUserMapper inviteUserMapper;
    @Autowired
    private InviteRecordMapper inviteRecordMapper;

    public String generateUnduplicatedInviteCode() {
        Boolean duplication = true;
        String code = null;
        while (duplication) {
            code = this.generateInviteCode();
            InviteUser inviteUser = inviteUserMapper.selectByCode(code);
            if (inviteUser == null) {
                duplication = false;
            }
        }
        return code;
    }

    public Boolean auth(Auth auth) {
//        Auth auth1 = new Auth(Auth.EMP, "%%MVC##MVC");
        Auth auth1 = new Auth(Auth.EMP, "332");
//        Auth auth2 = new Auth(Auth.TECH, "bet-man-returns");
        Auth auth2 = new Auth(Auth.TECH, "223");
        authList.add(auth1);
        authList.add(auth2);
        for (Auth sysAuth : authList) {
            if (sysAuth.getType().equals(auth.getType())
                    && sysAuth.getPassword().equals(auth.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private String generateInviteCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int x = random.nextInt(36);
            sb.append(inviteCodeChar[x]);
        }
        String code = sb.toString();
        return code;
    }

    public int batchUpdate(String csvData) {
        int updateCount = 0;
        List<Order> orders = this.parseCsv(csvData);
        for (Order order : orders) {
            if ("已完成".equals(order.getStatus())) {
                InviteRecord inviteRecord = inviteRecordMapper.selectByCode(order.getOrderCode());
                // 没有保存过的orderCode
                if (inviteRecord == null) {
                    InviteUser inviteUser = inviteUserMapper.selectByCode(order.getMemo());
                    // 有这个邀请码
                    if (inviteUser != null) {
                        inviteRecordMapper.insertInviteRecord(order);
                        inviteUserMapper.updateCount(order.getMemo(), inviteUser.getInviteCount() + order.getQuantity());
                        updateCount++;
                    }
                }
            }
        }
        return updateCount;
    }

    public List<Order> parseCsv(String csvData) {
        List<Order> list = new ArrayList<>();
        if (csvData != null) {
            String[] lines = csvData.split("\n");
            for (String line : lines) {
                String[] params = line.split(",");
                if (!params[0].equals("订单编号")) {
                    int quantity = Integer.parseInt(params[19]);
                    String memo = StringUtils.isEmpty(params[23]) ? "" : params[23].trim();
                    Order order = new Order(params[0], params[2], quantity, memo);
                    list.add(order);
                }
            }
        }
        return list;
    }

    public String md5(String str) throws NoSuchAlgorithmException {
        String md5Str = "";
        // 1 创建一个提供信息摘要算法的对象，初始化为md5算法对象
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 2 将消息变成byte数组
        byte[] input = str.getBytes();

        // 3 计算后获得字节数组,这就是那128位了
        byte[] buff = md.digest(input);

        // 4 把数组每一字节（一个字节占八位）换成16进制连成md5字符串
        md5Str = bytesToHex(buff);
        return md5Str;
    }

    public String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        // 把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];

            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }
}
