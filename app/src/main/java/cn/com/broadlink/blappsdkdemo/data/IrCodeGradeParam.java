package cn.com.broadlink.blappsdkdemo.data;

/**
 * Created by Administrator on 2018/9/8 0008.
 */

public class IrCodeGradeParam {

    private String namespace;
    private String name;
    private DataBean data;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String userId;
        private String scoreid;
        private ContentBean content;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getScoreid() {
            return scoreid;
        }

        public void setScoreid(String scoreid) {
            this.scoreid = scoreid;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public static class ContentBean {

            private int irId;
            private int score;
            private String brand;
            private String model;

            public int getIrId() {
                return irId;
            }

            public void setIrId(int irId) {
                this.irId = irId;
            }

            public int getScore() {
                return score;
            }

            public void setScore(int score) {
                this.score = score;
            }

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }
        }
    }
}
