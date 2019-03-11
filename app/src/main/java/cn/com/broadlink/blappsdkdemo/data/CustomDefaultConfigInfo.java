package cn.com.broadlink.blappsdkdemo.data;

import java.util.List;

/**
 * Created by Administrator on 2018/8/16 0016.
 */

public class CustomDefaultConfigInfo {
    private int status;
    private int error;
    private String msg;
    private List<ProductsBean> products;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ProductsBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsBean> products) {
        this.products = products;
    }

    public static class ProductsBean {

        private String pid;
        private String categoryid;
        private String name;
        private String icon;
        private List<FunctionListBean> functionList;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(String categoryid) {
            this.categoryid = categoryid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public List<FunctionListBean> getFunctionList() {
            return functionList;
        }

        public void setFunctionList(List<FunctionListBean> functionList) {
            this.functionList = functionList;
        }

        public static class FunctionListBean {
            private String function;
            private String name;

            public String getFunction() {
                return function;
            }

            public void setFunction(String function) {
                this.function = function;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
