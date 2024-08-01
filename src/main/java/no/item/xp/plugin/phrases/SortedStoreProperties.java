package no.item.xp.plugin.phrases;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

class SortedStoreProperties extends Properties {

  @Override
  public void store(OutputStream out, String comments) throws IOException {
    Properties sortedProps = new Properties() {
      @Override
      public Set<Map.Entry<Object, Object>> entrySet() {
      /*
       * Using comparator to avoid the following exception on jdk >=9:
       * java.lang.ClassCastException: java.base/java.util.concurrent.ConcurrentHashMap$MapEntry cannot be cast to java.base/java.lang.Comparable
       */
      Set<Map.Entry<Object, Object>> sortedSet = new TreeSet<Map.Entry<Object, Object>>(new Comparator<Map.Entry<Object, Object>>() {
        @Override
        public int compare(Map.Entry<Object, Object> o1, Map.Entry<Object, Object> o2) {
          return o1.getKey().toString().compareTo(o2.getKey().toString());
        }
      }
      );
      sortedSet.addAll(super.entrySet());
      return sortedSet;
    }

      @Override
      public Set<Object> keySet() {
        return new TreeSet<Object>(super.keySet());
      }

      @Override
      public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
      }

    };
    sortedProps.putAll(this);
    sortedProps.store(out, comments);
  }
}
