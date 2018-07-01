package coolou.com.coolou.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/7/1 0001.
 */

public class Now {

    @SerializedName("tem")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
