# TimeMerge
时间段合并  
一，前言  
-----  
在一些外卖app商家版中，有一个营业时间设置，要求用户输入几个时间段后，自动合并时间段，并排序好显示。  
现有其它需求是，营业时间不能跨天，即开始时间不能大于结束时间。  
二，核心代码实现  
-----  
```
/**
  * 时间段合并排序，限制开始时间小于结束时间，即在一天的时间里比较，不能跨天
  *
  * @param businessTimes
  * @return
  */
private List<BusinessTime> mergeTime(List<BusinessTime> businessTimes) {  
   // 首先必须对时间段排好序
   Collections.sort(businessTimes);
   Stack s = new Stack();
   Stack e = new Stack();
   s.push(businessTimes.get(0).getStart());
   e.push(businessTimes.get(0).getEnd());
   Log.e(">>>>>", "↓要合并的时间段↓");
   for (int i = 1; i < businessTimes.size(); i++) {
      BusinessTime time = businessTimes.get(i);
      Log.e(">>>>>", time.getStart() + " " + time.getEnd());
      // time.getStart()>e.peek()
      // 首先最小的时间段进栈，如果次小时间段的开始时间>最小时间段的结束时间，则说明次小时间段和最小时间段没有交集
      if (compare(time.getStart(), (String) e.peek()) == -1) {
         s.push(time.getStart());
         e.push(time.getEnd());
      }
      // time.getEnd()>e.peek()
      // 假如首先次小时间段的开始时间<最小时间段的结束时间(即上面的if语句块不执行)，
      // 即次小时间段的开始时间位于最小时间段里(因为时间段是从小到大排序的，次小时间段开始时间已经大于最小时间段的开始时间)，
      // (1)，如果次小时间段的结束时间>最小时间段的结束时间，则说明有交集、且合并时间段的结束时间是次小时间段的结束时间；
      else if (compare(time.getEnd(), (String) e.peek()) == -1) {
         e.pop();
         e.push(time.getEnd());
      }
      // (2)，如果次小时间段的结束时间<=最小时间段的结束时间，则说明最小时间段包含次小时间段。
      //else {}
   }
   businessTimes.clear();
   while (!s.empty()) {
      businessTimes.add(new BusinessTime((String) s.peek(), (String) e.peek()));
      e.pop();
      s.pop();
   }
   Collections.sort(businessTimes);
   Log.e(">>>>>", "↓已经合并的时间段↓");
   for (BusinessTime time : businessTimes) {
      Log.e(">>>>>", time.getStart() + " " + time.getEnd());
   }
   return businessTimes;
}
```  

```
class BusinessTime implements Comparable<BusinessTime> {
   private String start;
   private String end;
   BusinessTime(String start, String end) {
      this.start = start;
      this.end = end;
   }
   public String getStart() {
      return start;
   }
   public String getEnd() {
      return end;
   }
   public int compareTo(BusinessTime other) {
      if (compare(start, other.start) == 0) {
         return compare(other.end, end);
      }
      return compare(other.start, start);
   }
}
```  
三，预览  
-----  

![image](https://github.com/XYScience/TimeMerge/raw/master/screenshot/time_merge_screenshot.gif)
