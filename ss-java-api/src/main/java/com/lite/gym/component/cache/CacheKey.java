package com.lite.gym.component.cache;

public class CacheKey {
    private String name;
    private CacheKey parent;

    public CacheKey(String name, CacheKey parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getPath(){
        if(this.parent != null) {
            return this.parent.getPath() + ":" + this.name;
        }else {
            return this.name;
        }
    }

    public String getKey(String simpleKey){
        return getPath() +":" + simpleKey;
    }

    public final static CacheKey root = new CacheKey("lite-nave",null);
    public final static CacheKey sms  =  new CacheKey("sms",root);
    public final static CacheKey token  =  new CacheKey("token",root);
    public final static CacheKey user  =  new CacheKey("user",root);
    public final static CacheKey stage  =  new CacheKey("stage",root);
    public final static CacheKey wtoken  =  new CacheKey("wtoken",root);

    public static void main(String[] args){

        System.out.println(CacheKey.sms.getPath());
        System.out.println(CacheKey.sms.getKey("15977771238"));
        System.out.println(CacheKey.token.getPath());
        System.out.println(CacheKey.token.getKey("afs4345wefadzsd6d1gdfdsf456fads"));
    }
}
