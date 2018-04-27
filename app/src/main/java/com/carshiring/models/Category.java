package com.carshiring.models;

import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class Category {

    /**
     * message : success
     * response : {"cat":[{"category_id":"3","category_name":"Compect","category_image":"compect.png","code":3},{"category_id":"6","category_name":"Full size","category_image":"full_size.png","code":6},{"category_id":"4","category_name":"Intermediate","category_image":"Toyota_corolla_Intermediate.jpg","code":4},{"category_id":"7","category_name":"Premium","category_image":"Premier_car.jpg","code":7},{"category_id":"10","category_name":"Cargo van","category_image":"carvo_van.png","code":10},{"category_id":"9","category_name":"Mediam Passenger van","category_image":"medium_passenger_van.png","code":9}]}
     * status : true
     */

    private String message;
    private ResponseBean response;
    private boolean status;

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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
             * category_id : 3
             * category_name : Compect
             * category_image : compect.png
             * code : 3
             */

            private String category_id;
            private String category_name;
            private String category_image;
            private int code;

            public String getCategory_id() {
                return category_id;
            }

            public void setCategory_id(String category_id) {
                this.category_id = category_id;
            }

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
