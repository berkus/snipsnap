package org.snipsnap.test.interceptor.mock;

import org.apache.lucene.search.Hits;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import java.sql.Timestamp;
import java.util.List;

public class MockSnipSpace extends MockObject implements SnipSpace {
  public MockSnipSpace() {
    super();
  }

  // A snip is changed by the user (created, stored)
  public void changed(Snip snip) {
  }

  public void init() {
  }

  public void setETag() {
  }

  public int getSnipCount() {
    return 0;
  }

  public List getChanged() {
    return null;
  }

  public List getChanged(int count) {
    return null;
  }

  public List getAll() {
    return null;
  }

  public List getSince(Timestamp date) {
    return null;
  }

  public List getByDate(String start, String end) {
    return null;
  }

  public List getHot(int count) {
    return null;
  }

  public List getComments(Snip snip) {
    return null;
  }

  public List getByUser(String login) {
    return null;
  }

  public List getChildren(Snip snip) {
    return null;
  }

  public List getChildrenDateOrder(Snip snip, int count) {
    return null;
  }

  public List getChildrenModifiedOrder(Snip snip, int count) {
    return null;
  }

  public void reIndex() {
  }

  public Hits search(String queryString) {
    return null;
  }

  public Blog getBlog(String name) {
    return null;
  }

  public boolean exists(String name) {
    inc("exists");
    return false;
  }

  public Snip load(String name) {
    return null;
  }

  public void store(Snip snip) {
  }

  public Blog getBlog() {
    return null;
  }

  public void systemStore(Snip snip) {
  }

  public void delayedStrore(Snip snip) {
  }

  public Snip create(String name, String content) {
    inc("create");
    return null;
  }

  public void remove(Snip snip) {
  }

  public String getETag() {
    return null;
  }
}