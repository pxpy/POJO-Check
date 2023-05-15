开发辅助工具 <br>
正式项目需要打大量日志，有时候log.info(user)来记录user内的信息，<br>
但如果User类没有重写toString方法，在日志中记录的信息只有包名+类名，没有记录重要信息<br>
本插件检查VO、DTO、PO等是否重写toString或加@Data注解<br>
默认扫描model,vo,dto,po包路径下的类以及其它路径下以VO,PO,DTO结尾的类<br>
使用方法
1. 菜单栏tools-POJO Check，可在项目根目录输出报告，默认快捷键Ctrl+Shift+9，可以自行配置
2. 在idea底部Problems会自动检测并提示当前文件是否存在问题