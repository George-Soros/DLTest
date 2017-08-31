# DLTest
dynamic-load-apk插件开发demo,  dynamic-load-apk地址https://github.com/singwhatiwanna/dynamic-load-apk， 本demo的lib库就是dynamic-load-apk的

使用如下：
1:  新建new module , host和plugin





上面选择的是Phone & Tablet Module 而不是 Android Library,  新建的phone module表示这个module相当于单独的工程，可以自己打包apk，而如果是使用第二个就是一个库在build.gradle里面apply plugin: 'com.android.library'。

2:  将库lib进行编程成一个jar包

task makeJar(type: Copy) {
    delete 'build/libs/mylibrary.jar' //删除已经存在的jar包
    from('build/intermediates/bundles/release/')//从该目录下加载要打包的文件
    into('build/libs/')//jar包的保存目录
    include('classes.jar')//设置过滤，只打包classes文件
    rename('classes.jar', 'mylibrary.jar')//重命名，mylibrary.jar 根据自己的需求设置
}
makeJar.dependsOn(build)

lintOptions {
abortOnError false
}

最后的文件是mylibrary.jar 在build/libs下面, 然后把这个jar放到host和plugin的下面给用


plugin需要mylibrary.jar这个包，是需要activity继承DLBasePluginActivity来实现host后面加载plugin apk是可以代理插件的activity生命周期和功能等, 但是当plugin插件打包为apk给host用时

3：plugin打包为apk

需要注意的是：mylibrary.jar包不要打包到apk中，因为host中已经引用了这个jar包，所以依赖要使用provided来打包

dependencies {
provided fileTree(dir: 'libs', include: ['*.jar'])
// androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
// exclude group: 'com.android.support', module: 'support-annotations'
// })
// compile 'com.android.support:appcompat-v7:24.2.1'
// testCompile 'junit:junit:4.12'
}

将打包的apk，放到手机sd卡中，可以给host的动态加载了，其实正式情况是插件apk放到服务器，给host的下载的

4: host的逻辑开发，就是动态加载apk了，见host的MainActivity.class类



详细可以见这个:  http://blog.csdn.net/fuchaosz/article/details/51056947


 5： 遇到的坑


	* 如果插件用到so文件, 需要放到host中
	* 插件打包apk，引用的host的包或者库，需要provided
	* 
