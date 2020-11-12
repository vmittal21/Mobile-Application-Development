package edu.iit.knowyourgovernment;

import java.io.Serializable;

public class Channel implements Serializable {


       // private String googlePlusId;
        private String facebookId;
        private String twitterId;
        private String youtubeId;

        public String getFacebookId() {
            return facebookId;
        }

        public String getYoutubeId() {
        return youtubeId;
    }

        public String getTwitterId() {
            return twitterId;
        }

        public void setFacebookId(String facebookId) {
            this.facebookId = facebookId;
        }

        public void setTwitterId(String twitterId) {
            this.twitterId = twitterId;
        }

        public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }


}
