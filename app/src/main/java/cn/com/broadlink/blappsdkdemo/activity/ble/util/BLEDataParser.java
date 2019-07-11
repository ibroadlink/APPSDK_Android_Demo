package cn.com.broadlink.blappsdkdemo.activity.ble.util;

import com.taobao.accs.ErrorCode;

import java.util.Arrays;

import cn.com.broadlink.blappsdkdemo.activity.ble.bean.AddressInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.BalanceInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.BaseInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.MeterInfo;
import cn.com.broadlink.blappsdkdemo.activity.ble.bean.RechargeInfo;

public class BLEDataParser {

    private static final int REQUEST_BYTES_LENGTH = 128;
    private static final int REQUEST_RETURN_FAIL = -1000;
    private static final int PARSE_BYTES_FAIL = -1001;


    /**
     获取表号,通信地址

     @return
     */
    public static byte[] genGetAddress() {

        byte[] bytes = new byte[REQUEST_BYTES_LENGTH];
        int offset = 0;

        bytes[offset++] = (byte) 0x02;
        bytes[offset++] = (byte) 0x80;
        bytes[offset++] = (byte) 0x03;
        bytes[offset++] = (byte) 0xa1;

//        bytes[offset++] = (byte) 0xcb;
//        bytes[offset++] = (byte)0x54;

        byte[] checkBytes = Arrays.copyOfRange(bytes, 1, offset);
        int crc = BLECRC16Util.calcCrc16(checkBytes);
        byte[] crcBytes = intToBytes(crc);
        //低位在前，高位在后
        bytes[offset++] = crcBytes[3];
        bytes[offset++] = crcBytes[2];
        
        return Arrays.copyOfRange(bytes, 0, offset);
    }
    
    
    /**
     * 生成充值命令字节数组
     *
     * @param token 用户输入的20位数字token字符串
     * @param ssid ssid
     * @return 充值命令字节数组
     *
     * */
    public static byte[] genRechargeBytes(String token, String ssid) {
        byte[] bytes = new byte[REQUEST_BYTES_LENGTH];
        int offset = 0;
        bytes[offset++] = (byte) 0x02;
        bytes[offset++] = (byte) 0x80;
        bytes[offset++] = (byte) 0x25;
        bytes[offset++] = (byte) 0x55;
        bytes[offset++] = (byte) 0x68;

        byte[] ssidBytes = hex2Bytes(ssid2Address(ssid));
        for (byte b : ssidBytes) {
            bytes[offset++] = b;
        }

        bytes[offset++] = (byte) 0x68;
        bytes[offset++] = (byte) 0x00;
        bytes[offset++] = (byte) 0x16;
        bytes[offset++] = (byte) 0x34;
        bytes[offset++] = (byte) 0x13;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            ch += 0x33;
            byte b = (byte) ch;
            bytes[offset++] = b;
        }
        for (int i = 4; i <= offset - 1; i++) {
            bytes[offset] += bytes[i];
        }
        offset++;
        bytes[offset++] = (byte) 0x16;
        byte[] checkBytes = Arrays.copyOfRange(bytes, 1, offset);
        int crc = BLECRC16Util.calcCrc16(checkBytes);
        byte[] crcBytes = intToBytes(crc);
        //低位在前，高位在后
        bytes[offset++] = crcBytes[3];
        bytes[offset++] = crcBytes[2];
        return Arrays.copyOfRange(bytes, 0, offset);
    }

    /**
     * 生成余额查询命令字节数组
     *
     * @param ssid ssid
     * @return 余额查询命令字节数组
     *
     * */
    public static byte[] genBalanceBytes(String ssid) {
        byte[] bytes = new byte[REQUEST_BYTES_LENGTH];
        int offset = 0;
        bytes[offset++] = (byte) 0x02;
        bytes[offset++] = (byte) 0x80;
        bytes[offset++] = (byte) 0x14;
        bytes[offset++] = (byte) 0x55;
        bytes[offset++] = (byte) 0x68;

        byte[] ssidBytes = hex2Bytes(ssid2Address(ssid));
        for (byte b : ssidBytes) {
            bytes[offset++] = b;
        }

        bytes[offset++] = (byte) 0x68;
        bytes[offset++] = (byte) 0x00;
        bytes[offset++] = (byte) 0x05;
        bytes[offset++] = (byte) 0x35;
        bytes[offset++] = (byte) 0x13;
        bytes[offset++] = (byte) 0x63;
        bytes[offset++] = (byte) 0x63;
        bytes[offset++] = (byte) 0x6C;
        bytes[offset++] = (byte) 0xB6;
        bytes[offset++] = (byte) 0x16;
        byte[] checkBytes = Arrays.copyOfRange(bytes, 1, offset);
        int crc = BLECRC16Util.calcCrc16(checkBytes);
        byte[] crcBytes = intToBytes(crc);
        //低位在前，高位在后
        bytes[offset++] = crcBytes[3];
        bytes[offset++] = crcBytes[2];
        return Arrays.copyOfRange(bytes, 0, offset);
    }

    /**
     * 生成参数查询命令字节数组
     *
     * @param ssid ssid
     * @return 参数查询命令字节数组
     *
     * */
    public static byte[] genInquiryBytes(String ssid) {
        byte[] bytes = new byte[REQUEST_BYTES_LENGTH];
        int offset = 0;
        bytes[offset++] = (byte) 0x02;
        bytes[offset++] = (byte) 0x80;
        bytes[offset++] = (byte) 0x14;
        bytes[offset++] = (byte) 0x55;
        bytes[offset++] = (byte) 0x68;

        byte[] ssidBytes = hex2Bytes(ssid2Address(ssid));
        for (byte b : ssidBytes) {
            bytes[offset++] = b;
        }

        bytes[offset++] = (byte) 0x68;
        bytes[offset++] = (byte) 0x00;
        bytes[offset++] = (byte) 0x05;
        bytes[offset++] = (byte) 0x35;
        bytes[offset++] = (byte) 0x13;
        bytes[offset++] = (byte) 0x63;
        bytes[offset++] = (byte) 0x6A;
        bytes[offset++] = (byte) 0x6B;
        bytes[offset++] = (byte) 0xBC;
        bytes[offset++] = (byte) 0x16;
        byte[] checkBytes = Arrays.copyOfRange(bytes, 1, offset);
        int crc = BLECRC16Util.calcCrc16(checkBytes);
        byte[] crcBytes = intToBytes(crc);
        //低位在前，高位在后
        bytes[offset++] = crcBytes[3];
        bytes[offset++] = crcBytes[2];
        return Arrays.copyOfRange(bytes, 0, offset);
    }

    /**
     * 将字节数组转化成相应的数据实体，数据为ASCII码，转化时需-0x33
     *
     * @param bytes 待转换的字节数组
     * @return 生成的数据实体
     *
     * */
    public static BaseInfo parseBytes(byte[] bytes) {


        try {
            if (bytes[0] == (byte)0x02 && bytes[1] == (byte)0x80 && bytes[3] == (byte)0xa0 && bytes[4] == (byte)0x00 && bytes[5] == (byte)0x0b) {//获取表号
                final AddressInfo addressInfo = new AddressInfo();
                
                int len = bytes[6];
                final StringBuilder stringBuilder = new StringBuilder();
    
                try {
                    for (int i = 6+len; i >6; i--) {
                        stringBuilder.append((bytes[i] - 0x30));
                    }
    
                    addressInfo.setAddress(stringBuilder.toString());
                    addressInfo.setState(ErrorCode.SUCCESS);
                    return addressInfo;
                } catch (Exception e) {
                    e.printStackTrace();
                    addressInfo.setState(REQUEST_RETURN_FAIL);
                    return addressInfo;
                }
    
            }else if (bytes[14] == (byte) 0x34 && bytes[15] == (byte) 0x13 && bytes[16] == (byte) 0x33) {//充值操作
                if (bytes[17] == (byte) 0x33) {//成功
                    //充值额度，低位在前
                    byte[] rechargeValueBytes = new byte[4];
                    byte b1 = (byte) (bytes[20] - (byte) 0x33);
                    byte b2 = (byte) (bytes[21] - (byte) 0x33);
                    byte b3 = (byte) (bytes[22] - (byte) 0x33);
                    byte b4 = (byte) (bytes[23] - (byte) 0x33);
                    rechargeValueBytes[0] = b1;
                    rechargeValueBytes[1] = b2;
                    rechargeValueBytes[2] = b3;
                    rechargeValueBytes[3] = b4;
                    double rechargeValue = bytes2Int(rechargeValueBytes) * 0.01;
                    //余额，低位在前
                    byte[] balanceBytes = new byte[4];
                    b1 = (byte) (bytes[26] - (byte) 0x33);
                    b2 = (byte) (bytes[27] - (byte) 0x33);
                    b3 = (byte) (bytes[28] - (byte) 0x33);
                    b4 = (byte) (bytes[29] - (byte) 0x33);
                    balanceBytes[0] = b1;
                    balanceBytes[1] = b2;
                    balanceBytes[2] = b3;
                    balanceBytes[3] = b4;
                    double balance = bytes2Int(balanceBytes) * 0.01;
    
                    RechargeInfo info = new RechargeInfo();
                    info.setState(ErrorCode.SUCCESS);
                    info.setRechargeValue(rechargeValue);
                    info.setBalance(balance);
                    return info;
                } else {
                    RechargeInfo info = new RechargeInfo();
                    info.setState(REQUEST_RETURN_FAIL);
                    return info;
                }
            } else if (bytes[14] == (byte) 0x35 && bytes[15] == (byte) 0x13 && bytes[16] == (byte) 0x63
                            && bytes[17] == (byte) 0x63 && bytes[18] == (byte) 0x6C) {//查询余额操作
                if (bytes[19] == 0x33) {//成功
                    byte[] balanceBytes = new byte[4];
                    byte b1 = (byte) (bytes[22] - (byte) 0x33);
                    byte b2 = (byte) (bytes[23] - (byte) 0x33);
                    byte b3 = (byte) (bytes[24] - (byte) 0x33);
                    byte b4 = (byte) (bytes[25] - (byte) 0x33);
                    balanceBytes[0] = b1;
                    balanceBytes[1] = b2;
                    balanceBytes[2] = b3;
                    balanceBytes[3] = b4;
                    double balance = bytes2Int(balanceBytes) * 0.01;
                    BalanceInfo info = new BalanceInfo();
                    info.setState(ErrorCode.SUCCESS);
                    info.setBalance(balance);
                    return info;
                } else {
                    BalanceInfo info = new BalanceInfo();
                    info.setState(REQUEST_RETURN_FAIL);
                    return info;
                }
            } else if (bytes[14] == (byte) 0x35 && bytes[15] == (byte) 0x13 && bytes[16] == (byte) 0x63
                    && bytes[17] == (byte) 0x6A && bytes[18] == (byte) 0x6B) {//查询电表参数操作
                if (bytes[19] == 0x33) {//成功
                    //余额
                    byte[] balanceBytes = new byte[4];
                    byte b1 = (byte) (bytes[26] - (byte) 0x33);
                    byte b2 = (byte) (bytes[27] - (byte) 0x33);
                    byte b3 = (byte) (bytes[28] - (byte) 0x33);
                    byte b4 = (byte) (bytes[29] - (byte) 0x33);
                    balanceBytes[0] = b1;
                    balanceBytes[1] = b2;
                    balanceBytes[2] = b3;
                    balanceBytes[3] = b4;
                    double balance = bytes2Int(balanceBytes) * 0.01;
    
                    //时间
                    int minute = bytes[33] - (byte) 0x33;
                    int hour = bytes[34] - (byte) 0x33;
                    int day = bytes[35] - (byte) 0x33;
                    int month = bytes[36] - (byte) 0x33;
                    int year = bytes[37] - (byte) 0x33;
                    String time = day + "-" + month + "-" + year + " " + hour + ":" + minute;
    
                  /*  //电表状态
                    int meterState = MeterInfo.METER_STATE_NONE;
                    byte bms1 = (byte) (bytes[42] - (byte) 0x33);
                    for(int i = 7; i >= 0; i--){
                        if(((bms1 >> i) & 0x01) == 1) {
                            if (7 - i == 0) meterState = meterState | MeterInfo.METER_STATE_METER_COVER_OPEN;
                            else if (7 - i == 1) meterState = meterState | MeterInfo.METER_STATE_TERMINAL_COVER_OPEN;
                            else if (7 - i == 2) meterState = meterState | MeterInfo.METER_STATE_PHASE_A_OVERVOLTAGE;
                            else if (7 - i == 3) meterState = meterState | MeterInfo.METER_STATE_PHASE_B_OVERVOLTAGE;
                            else if (7 - i == 4) meterState = meterState | MeterInfo.METER_STATE_PHASE_C_OVERVOLTAGE;
                            else if (7 - i == 5) meterState = meterState | MeterInfo.METER_STATE_PHASE_A_UNDERVOLTAGE;
                            else if (7 - i == 6) meterState = meterState | MeterInfo.METER_STATE_PHASE_B_UNDERVOLTAGE;
                            else if (7 - i == 7) meterState = meterState | MeterInfo.METER_STATE_PHASE_C_UNDERVOLTAGE;
                        }
                    }
                    byte bms2 = (byte) (bytes[41] - (byte) 0x33);
                    for(int i = 7; i >= 0; i--){
                        if(((bms2 >> i) & 0x01) == 1) {
                            if (7 - i == 0) meterState = meterState | MeterInfo.METER_STATE_PHASE_A_REVERSE;
                            else if (7 - i == 1) meterState = meterState | MeterInfo.METER_STATE_PHASE_B_REVERSE;
                            else if (7 - i == 2) meterState = meterState | MeterInfo.METER_STATE_PHASE_C_REVERSE;
                            else if (7 - i == 3) meterState = meterState | MeterInfo.METER_STATE_BYPASS;
                            else if (7 - i == 4) meterState = meterState | MeterInfo.METER_STATE_THREE_PHASE_UNBALANCE;
                            else if (7 - i == 5) meterState = meterState | MeterInfo.METER_STATE_OVERLOAD;
                            else if (7 - i == 6) meterState = meterState | MeterInfo.METER_STATE_MAGNETIC_FIELD;
                            else if (7 - i == 7) meterState = meterState | MeterInfo.METER_STATE_MODULE_COVER_OPEN;
                        }
                    }*/
    
                    //继电器状态
                    byte brs = (byte) (bytes[45] - (byte) 0x33);
                    int relayState = MeterInfo.RELAY_STATE_OPEN;
                    if (brs == 0x50) {
                        relayState = MeterInfo.RELAY_STATE_OPEN;
                    } else if (brs == 0x5F) {
                        relayState = MeterInfo.RELAY_STATE_CLOSE;
                    }
    
                 /*   //电表模式
                    byte bmm = (byte) (bytes[51] - (byte) 0x33);
                    int meterMode = MeterInfo.METER_MODE_REPAY;
                    if (bmm == 0x00) {
                        meterMode = MeterInfo.METER_MODE_REPAY;
                    } else if (bmm == 0x5A) {
                        meterMode = MeterInfo.METER_MODE_COMMON;
                    }*/
    
                    //正向有功总功率
                    byte[] bpapBytes = new byte[4];
                    byte bpap1 = (byte) (bytes[54] - (byte) 0x33);
                    byte bpap2 = (byte) (bytes[55] - (byte) 0x33);
                    byte bpap3 = (byte) (bytes[56] - (byte) 0x33);
                    byte bpap4 = (byte) (bytes[57] - (byte) 0x33);
                    bpapBytes[0] = bpap1;
                    bpapBytes[1] = bpap2;
                    bpapBytes[2] = bpap3;
                    bpapBytes[3] = bpap4;
                    double positiveActivePower = bytes2Int(bpapBytes) * 0.0001;
    
                    //正向有功总能量
                    byte[] bpaeBytes = new byte[4];
                    byte bpae1 = (byte) (bytes[60] - (byte) 0x33);
                    byte bpae2 = (byte) (bytes[61] - (byte) 0x33);
                    byte bpae3 = (byte) (bytes[62] - (byte) 0x33);
                    byte bpae4 = (byte) (bytes[63] - (byte) 0x33);
                    bpaeBytes[0] = bpae1;
                    bpaeBytes[1] = bpae2;
                    bpaeBytes[2] = bpae3;
                    bpaeBytes[3] = bpae4;
                    double positiveActiveEnergy = bytes2Int(bpaeBytes) * 0.01;
    
                    //正向有功费率1能量
                    byte[] bpaerBytes = new byte[4];
                    byte bpaer1 = (byte) (bytes[64] - (byte) 0x33);
                    byte bpaer2 = (byte) (bytes[65] - (byte) 0x33);
                    byte bpaer3 = (byte) (bytes[66] - (byte) 0x33);
                    byte bpaer4 = (byte) (bytes[67] - (byte) 0x33);
                    bpaerBytes[0] = bpaer1;
                    bpaerBytes[1] = bpaer2;
                    bpaerBytes[2] = bpaer3;
                    bpaerBytes[3] = bpaer4;
                    double positiveActiveEnergyRate1 = bytes2Int(bpaerBytes) * 0.01;
    
                    //正向有功费率2能量
                    bpaer1 = (byte) (bytes[68] - (byte) 0x33);
                    bpaer2 = (byte) (bytes[69] - (byte) 0x33);
                    bpaer3 = (byte) (bytes[70] - (byte) 0x33);
                    bpaer4 = (byte) (bytes[71] - (byte) 0x33);
                    bpaerBytes[0] = bpaer1;
                    bpaerBytes[1] = bpaer2;
                    bpaerBytes[2] = bpaer3;
                    bpaerBytes[3] = bpaer4;
                    double positiveActiveEnergyRate2 = bytes2Int(bpaerBytes) * 0.01;
    
                    //正向有功费率3能量
                    bpaer1 = (byte) (bytes[72] - (byte) 0x33);
                    bpaer2 = (byte) (bytes[73] - (byte) 0x33);
                    bpaer3 = (byte) (bytes[74] - (byte) 0x33);
                    bpaer4 = (byte) (bytes[75] - (byte) 0x33);
                    bpaerBytes[0] = bpaer1;
                    bpaerBytes[1] = bpaer2;
                    bpaerBytes[2] = bpaer3;
                    bpaerBytes[3] = bpaer4;
                    double positiveActiveEnergyRate3 = bytes2Int(bpaerBytes) * 0.01;
    
                    //正向有功费率4能量
                    bpaer1 = (byte) (bytes[76] - (byte) 0x33);
                    bpaer2 = (byte) (bytes[77] - (byte) 0x33);
                    bpaer3 = (byte) (bytes[78] - (byte) 0x33);
                    bpaer4 = (byte) (bytes[79] - (byte) 0x33);
                    bpaerBytes[0] = bpaer1;
                    bpaerBytes[1] = bpaer2;
                    bpaerBytes[2] = bpaer3;
                    bpaerBytes[3] = bpaer4;
                    double positiveActiveEnergyRate4 = bytes2Int(bpaerBytes) * 0.01;
    
                    //秘钥版本号
                    int secretKeyVision = bytes[86] - (byte) 0x33;
    
                    //费率
                    double rate = (double) (bytes[89] - (byte) 0x33);
    
                    MeterInfo info = new MeterInfo();
                    info.setBalance(balance);
                    info.setTime(time);
                    //info.setState(meterState);
                    info.setRelayState(relayState);
                    info.setPositiveActivePower(positiveActivePower);
                    info.setPositiveActiveEnergy(positiveActiveEnergy);
                    info.setPositiveActiveEnergyRate1(positiveActiveEnergyRate1);
                    info.setPositiveActiveEnergyRate2(positiveActiveEnergyRate2);
                    info.setPositiveActiveEnergyRate3(positiveActiveEnergyRate3);
                    info.setPositiveActiveEnergyRate4(positiveActiveEnergyRate4);
                    info.setSecretKeyVision(secretKeyVision);
                    info.setRate(rate);
                    return info;
                } else {
                    MeterInfo info = new MeterInfo();
                    info.setState(REQUEST_RETURN_FAIL);
                    return info;
                }
    
            } else {
                BaseInfo info = new BaseInfo();
                info.setState(PARSE_BYTES_FAIL);
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * int到byte[] 由高位到低位
     *
     * @param i 需要转换为byte数组的整行值。
     * @return byte数组
     *
     */
    public static byte[] intToBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    /**
     * byte[]到int
     *
     * @param bytes 需要转换为int的byte数组。
     * @return int
     *
     */
    public static int bytes2Int(byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    public static String ssid2Address(String ssid) {
        StringBuilder address = new StringBuilder();
        if (ssid.length() % 2 == 1) {
            ssid = "0" + ssid;
        }
        for (int i = ssid.length() - 2; i >= 0; i -= 2) {
            address.append(ssid.substring(i, i + 2));
        }
        return address.toString();
    }

    public static byte[] hex2Bytes(String hex) {
        byte[] result = new byte[hex.length() / 2];
        int j = 0;
        for(int i = 0; i < hex.length(); i += 2) {
            result[j++] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return result;
    }
}
