# Android中一种高效省资源的汉字转拼音的实现

我在开发Android项目中经常会遇到汉字转拼音的需求，主要是一些按拼音排序，首字母本地搜索匹配的需求，这些需求的特点是这样的：不要求处理声调和多音字，但是对算法效率，内存占用要求较高。下面我分享一下我方案和实现。

-------------------
###基于unicode码的索引实现

原理：
我们常用的汉字大概有19000多个，在unicode编码里是连续的，范围是u4E00-u9FA5。unicode编撰的人可能对汉子不是很了解，所以编码的顺序没什么规律。汉语拼音的组合，比如这些：chang,dao,cui。这些组合一共有400多个。
我想到的方案是将所有拼音组合作为一张表。每个汉字unicode码对应一个拼音组合表的索引。汉字转拼音的时候直接通过unicode码找到对应索引，然后查到对应的拼音。这样实现的算法复杂度为O(1)。
内存占用稍微大一点，两张表一共占用为42k字节。

实现：
1，拼音组合的表，作为一个常量数据，类似下面这样：
![这里写图片描述](http://img.blog.csdn.net/20170828131039881?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvam9rZXI1MzU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
这个常量数组会常驻内存，内存占用大概2k左右。
2，unicode索引文件，索引文件的作用是通过汉字unicode码找到拼音表对应的拼音，所以最简单的实现就是两列的表，第一列是unicode（汉字所在范围都是16位，2字节），第二列是拼音表中的索引（0-407，需要16位表示，2字节）。类似下面这样：
![这里写图片描述](http://img.blog.csdn.net/20170828132658141?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvam9rZXI1MzU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
但其实仔细一下，第一列unicode码是连续的，如果全部减去4E00，第一列就变成0，1，2，3，4...这样，其实没用必要存在，转拼音时候只要把汉字unicode减去4E00就能找到对应的索引。所以去掉第一列这个文件就是一个索引的数组。文件大小是40k，也需要常驻内存。
简单的流程示意如下：
![这里写图片描述](http://img.blog.csdn.net/20170828134734305?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvam9rZXI1MzU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
