# PDPermission
PDPermission

***

> Compile
```ruby
  	allprojects {
  		repositories {
  			...
  			maven { url 'https://jitpack.io' }
  		}
  	}

  	dependencies {
  		compile 'com.github.pdliugithub:PDPermission:v1.1.1'
  	}
```
>Usage
```ruby
  	一、Request single permission.
  	  	//New PermissionManager instance.
  	  	mPermissionManager = PermissionManager.newInstance();

   	  	//Callback
  	  	mPermissionManager.setPermissionResultSingleCallback(resultCallback);

  	  	private PermissionManager.PermissionResultCallback resultCallback = new PermissionManager.PermissionResultCallback() {
  	  	  	@Override
  	  	  	public void denyResultCall(@DeniedType int denyType) {
  	  	  	  	//权限拒签Call
  	  	  	  	//denyType --> DeniedType
  	  	  	  	LogUtil.e(KEY_TAG, "--------------------------grantType：" + denyType);
  	  	  	}

  	  	  	@Override
  	  	  	public void grantResultCall(@GrantedType int grantType) {
  	  	  	  	//权限授权Call
  	  	  	  	//grantType --> GrantedType
  	  	  	  	LogUtil.e(KEY_TAG, "--------------------------grantType：" + grantType);
  	  	  	}
  	  	};

  	  	//Check and request permissions become one.s
  	  	mPermissionManager.checkPermissionSingle(Activity activity, String permission, int requestCode)

  	  	//Implements ActivityCompat.OnRequestPermissionsResultCallback  and...
  	  	@Override
  	  	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
  	  	  	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

  	  	  	//Add the line code
  	  	  	mPermissionManager.requestPermissionSingleResult(requestCode, permissions, grantResults);
  	  	}
  	二、Request many permission.
  	  	//New PermissionManager instance.
  	  	mPermissionManager = PermissionManager.newInstance();

  	  	//Callback
  	  	mPermissionManager.setPermissionManyResultCallback(manyResultCallback);

  	  	  	private PermissionManager.PermissionManyResultCallback manyResultCallback = new PermissionManager.PermissionManyResultCallback() {
  	  	  	  	@Override
  	  	  	  	public String resultMany(LinkedHashMap<String, Integer> result) {
  	  	  	  	  	//String ---> request permission name
  	  	  	  	  	//Integer ---> request permission result .Between GrantedType with DeniedType.

  	  	  	  	  	Set<Map.Entry<String, Integer>> set = result.entrySet();
  	  	  	  	  	Iterator<Map.Entry<String, Integer>> it = set.iterator();

  	  	  	  	  	while (it.hasNext()) {
  	  	  	  	  	  	Map.Entry<String, Integer> next = it.next();
  	  	  	  	  	  	String key = next.getKey();
  	  	  	  	  	  	Integer value = next.getValue();

  	  	  	  	  	  	LogUtil.e(KEY_TAG, "---------------------key:\t" + key + "\t\t value:\t" + value);
  	  	  	}

  	  	  	  	  	return null;
  	  	  	  	}
  	  	  	};

  	  	//Check and request permissions become one.s
  	  	mPermissionManager.checkPermissionMany(Activity activity, String[] permissions, int requestCode);

  	  	//Implements ActivityCompat.OnRequestPermissionsResultCallback  and...
  	   	@Override
  	  	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
  	  	  	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

  	  	  	//Add the line code
  	  	  	mPermissionManager.requestPermissionManyResult(requestCode, permissions, grantResults);
  	  	}


```
>  Note: When you use a checkPermissionSingle, you can also use checkPermissionMany.
  	  	  	But i don't recommend it and you should make a distinction.

> Medal

 [![](https://jitpack.io/v/pdliugithub/PDPermission.svg)](https://jitpack.io/#pdliugithub/PDPermission)
