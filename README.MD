编译原理上机作业
# 作业1
实现C语言基础下的test语言实现阶乘
项目包含以下文件：
TESTmain.c : 主程序，先后调用词法分析、语法分析、语义分析和代码生成
old.TESTscan.c : 词法分析
TESTparse.c : 语法分析、语义分析以及TEST机的汇编代码生成.分析过程中如果有错误在这里报出.
我会另外实现纯逻辑上的java版本

## TEST语言描述:
TEST 语言的程序结构很简单，它在语法上相当于C的函数体，即由一对大
括号括起来的语句序列，没有过程或函数。声明语句、表达式语句以及控制语句
的写法都与C语言类似，但规定：一条声明语句只能声明一个整型简单变量，没
有数组；控制语句只是if、while和for三个语句，这三个控制语句本身也可包含
语句序列；表达式仅限于布尔表达式和整型算术表达式，布尔表达式由对两个算
术表达式的比较组成，该比较实用<、<=、>、>=、==和!=比较算符；算术表达
式可以包括整型常量、变量以及+、-、*、/这4个运算符。另外，还可以有复合
语句。为了能实现输入输出，又添加了read语句和write语句。TEST语言还可
以有注释，注释用/*和*/括起来，但注释不能嵌套。 

## 规定的词法规则 :
### 文字形式的定义:
TEST 语言的所有变量都是整型变量，具有if、while、for等控制语句。注释
用/*和*/括起来，但注释不能嵌套。TEST的表达式局限于布尔表达式和算术表达
式。 
TEST 语言的单词符号有： 
标识符：字母开头,后接字母或数字。 
保留字(它是标识符的子集)：if、else、for、 while、do、int。 
无符号整数：由0~9数字组成。 
分界符：如+、-、*、/、(、)、;、, 等单分界符；还有双字符分界符>=、<=、!=、
==等。 
注释符：用/*和*/括起来。 
词法分析程序并不输出注释,在词法分析阶段注释的内容将被删掉。为了从
源程序字符流中正确识别出各类单词符号，相邻的标识符整数或保留字之间至少
要用一个空格分开。 
### 数学形式的定义:
<identifier>-><letter>|<identifier><letter>l<identifier><digit> 
<number>-> <digit>|<number><digit> 
<letter>-> a|b|..|z|A|B|..|Z 
<digit>-> 1|2|…|9|0 
<singleword>-> +|-|*|/|=|(|)|{|}|:|,|;<|>|! 
<doubleword>-> >=|<=|!=|== 
<comment first>-> /* 
<commentlast>-> */

## 结果
运行java/src/Lex/test下的LexTest.java
# 作业2
实现词法分析器
## 结果
运行java/src/Lex/test下的RegexTest.java
# 作业3
语法分析1：自顶向上的语法分析
在这个pdf中，}是终结符，\[]是一种标记，表示”可选“
规则13要多读一位
## 结果
运行java/src/Syntac/test/UnitTest.java的test_problem1~3
# 作业4
实现LL语法分析
这个最为复杂，构造FIRST集合、构造FOLLOW集合、构造分析决策表
然后再进行LL分析
## 结果
运行java/src/Syntac/test/UnitTest.java的test_problem4~5
## 关于语法分析
关于我的Blocks链的说明：
如果一个if里面有很多句话，也有else分支，那么：
if(bool_expr){
    stat1;
    stat2;
}
else{
    stat3;
    stat4;
}
会转化成：
<if_stat>
    <bool_expr>
    <compound_list>
        <stat_list>
            <stat1>
            <stat2>
    <compound_list>
        <stat_list>
            <stat3>
            <stat4>
# 作业5
实现将语法树转化为栈式抽象汇编语言的程序
## 结果
运行java/src/Semantic/AsmSpawner.java

# 作业 6
实现能够运行栈式汇编语言的程序

## 结果
运行java/src/Semantic/AsmRunner.java
或者直接运行java/src/Main.java

# 总结
2500行代码，菜的不行
