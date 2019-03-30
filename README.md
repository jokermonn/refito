能力：你懂得

使用方式：你懂得

初始化：需要 `addCallAdapterFactory(RxJava2ZipCallAdapterFactory.create())`，如果需要使用 RxJava2 的原生 CallAdapterFactory 的话，`RxJava2CallAdapterFactory.create()` 的添加需要在 `RxJava2ZipCallAdapterFactory.create()` 之后。