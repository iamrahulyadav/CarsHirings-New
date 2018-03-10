package com.carshiring.models;

import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class Category {


    /**
     * message : success
     * response : {"cat":[{"category_name":"Mini","category_image":"New_Ford_Fiesta__Mini.JPG","code":1},{"category_name":"Mini","category_image":"New_Ford_Fiesta__Mini.JPG","code":33}]}
     * status : true
     */

    private String message;
    private ResponseBean response;
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class ResponseBean {
        private List<CatBean> cat;

        public List<CatBean> getCat() {
            return cat;
        }

        public void setCat(List<CatBean> cat) {
            this.cat = cat;
        }

        public static class CatBean {
            /**
             * category_name : Mini
             * category_image : New_Ford_Fiesta__Mini.JPG
             * code : 1
             */

            private String category_name;
            private String category_image;
            private int code;

            public String getCategory_name() {
                return category_name;
            }

            public void setCategory_name(String category_name) {
                this.category_name = category_name;
            }

            public String getCategory_image() {
                return category_image;
            }

            public void setCategory_image(String category_image) {
                this.category_image = category_image;
            }

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }
        }
    }
}
