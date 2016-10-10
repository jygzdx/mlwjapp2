package com.mlxing.chatui.daoyou.entity;

/**
 * Created by Administrator on 2016/9/28.
 */
public class UserInfoEntity {

    /**
     * code : 200
     * msg : 成功
     * page :
     * pageSize :
     * pageCount :
     * rowCount :
     * total :
     * result : {"mid":"892","truename":"孙林林","idCard":"41132319920326177X",
     * "idPic1":"2016-05-30/0efce1f89cb4161fb56eac9e67b549fc.jpg",
     * "idPic2":"2016-05-30/0940d2b29f9371ea4fd4637fbe314ee1.jpg","dyCard":"D-6495-5464",
     * "phone":"15555555555","dyState":"3","description":"","email":"","tag":"","grade":"初级导游",
     * "level":"1","age":"","fansNum":"","strategyNum":"","language":"","area":"","intro":"",
     * "bgImgUrl":"ledao/1.jpg","distance":"0"}
     */

    private String code;
    private String msg;
    /**
     * mid : 892
     * truename : 孙林林
     * idCard : 41132319920326177X
     * idPic1 : 2016-05-30/0efce1f89cb4161fb56eac9e67b549fc.jpg
     * idPic2 : 2016-05-30/0940d2b29f9371ea4fd4637fbe314ee1.jpg
     * dyCard : D-6495-5464
     * phone : 15555555555
     * dyState : 3
     * description :
     * email :
     * tag :
     * grade : 初级导游
     * level : 1
     * age :
     * fansNum :
     * strategyNum :
     * language :
     * area :
     * intro :
     * bgImgUrl : ledao/1.jpg
     * distance : 0
     */

    private ResultEntity result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public static class ResultEntity {
        private String mid;
        private String truename;
        private String idCard;
        private String idPic1;
        private String idPic2;
        private String dyCard;
        private String phone;
        private String dyState;
        private String description;
        private String email;
        private String tag;
        private String grade;
        private String level;
        private String age;
        private String fansNum;
        private String strategyNum;
        private String language;
        private String area;
        private String intro;
        private String bgImgUrl;
        private String distance;

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getTruename() {
            return truename;
        }

        public void setTruename(String truename) {
            this.truename = truename;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getIdPic1() {
            return idPic1;
        }

        public void setIdPic1(String idPic1) {
            this.idPic1 = idPic1;
        }

        public String getIdPic2() {
            return idPic2;
        }

        public void setIdPic2(String idPic2) {
            this.idPic2 = idPic2;
        }

        public String getDyCard() {
            return dyCard;
        }

        public void setDyCard(String dyCard) {
            this.dyCard = dyCard;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDyState() {
            return dyState;
        }

        public void setDyState(String dyState) {
            this.dyState = dyState;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getFansNum() {
            return fansNum;
        }

        public void setFansNum(String fansNum) {
            this.fansNum = fansNum;
        }

        public String getStrategyNum() {
            return strategyNum;
        }

        public void setStrategyNum(String strategyNum) {
            this.strategyNum = strategyNum;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getBgImgUrl() {
            return bgImgUrl;
        }

        public void setBgImgUrl(String bgImgUrl) {
            this.bgImgUrl = bgImgUrl;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }
}
