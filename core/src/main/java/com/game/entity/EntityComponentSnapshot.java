package com.game.entity;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.ints.AbstractIntList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongArrayList;

public class EntityComponentSnapshot {
  LongArrayList x;
  LongArrayList lastX;
  LongArrayList y;
  LongArrayList lastY;
  LongArrayList direction;
  LongArrayList lastDirection;
  LongArrayList hp;
  BooleanArrayList isSelected;
  IntArrayList selected;
  BooleanArrayList isActive;
  IntOpenHashSet activeId;
  IntOpenHashSet inactiveId;
  IntArrayList nextInCell;
  IntArrayList unitType;
  IntArrayList visible;
  IntArrayList generation;
  
  LongArrayList tags;
  LongArrayList abilities;
  
  LongArrayList effects;
  
  LongArrayList effectMask;
  IntArrayList effectTimer;
  IntArrayList effected;
  
  //for syncing with the other snapshot
  LongArrayList actionValue1;
  LongArrayList actionValue2;
  LongArrayList actionType;
  IntArrayList actionId;
  IntArrayList globalActionBuffer;
  
  
  private void reset(AbstractIntList collection){
    for (int i = 0; i < collection.size(); i++){
      collection.set(i, -1);
    }
  }
  
  EntityComponentSnapshot(int size){
    x = new LongArrayList(size);
    lastX = new LongArrayList(size);
    y = new LongArrayList(size);
    lastY = new LongArrayList(size);
    direction = new LongArrayList(size);
    lastDirection = new LongArrayList(size);
    hp = new LongArrayList(size);
    isSelected = new BooleanArrayList(size);
    selected = new IntArrayList(size);
    isActive = new BooleanArrayList(size);
    activeId = new IntOpenHashSet(size);
    inactiveId = new IntOpenHashSet(size);
    nextInCell = new IntArrayList(size);
    unitType = new IntArrayList(size);
    visible = new IntArrayList(size);
    generation = new IntArrayList(size);
    
    tags = new LongArrayList(size);
    abilities = new LongArrayList(size);
    
    effectMask = new LongArrayList();
    effects = new LongArrayList(size);
    effectTimer = new IntArrayList();
    effected = new IntArrayList();
    
    actionId = new IntArrayList(size);
    actionValue1 = new LongArrayList();
    actionValue2 = new LongArrayList();
    actionType = new LongArrayList();
    
    globalActionBuffer = new IntArrayList();
    
    for (int i = 0; i<size; i++) inactiveId.add(i);
    
    x.size(size);
    lastX.size(size);
    y .size(size);
    lastY.size(size);
    direction.size(size);
    lastDirection.size(size);
    hp.size(size);
    isSelected.size(size);
    isActive.size(size);
    nextInCell.size(size);
    unitType.size(size);
    generation.size(size);
    
    tags.size(size);
    abilities.size(size);
    
    effects.size(size);
    
    //set to -1 so it won't think something of id 0 is there
    reset(nextInCell);
    reset(unitType);
  }
  
  void sortVisible(){
    visible.unstableSort(null);
  }
  void clearVisible(){
    visible.clear();
  }
  void addVisible(int id){
    visible.add(id);
  }
  //TODO: fill EntityComponentAction and make these methods add it to actionBUffer and add the id to newActions
  //TODO: call the draw commands after i actually get Vulkan working
  void clearNextInCell(){
    for (int index : activeId){
      nextInCell.set(index, -1);
    }
  }
  void addNextInCell(int id, int nextId){
    nextInCell.set(id, nextId);
  }
  
  public void recordAction(int id, long value, long action){
    actionId.add(id);
    actionType.add(action);
    actionValue1.add(value);
    actionValue2.add(-1);
  }
  public void recordAction(int id, long value1, long value2, long action){
    actionId.add(id);
    actionType.add(action);
    actionValue1.add(value1);
    actionValue2.add(value2);
  }
  
  
  void setX(long x, int id){
    recordAction(id, x, EntityComponentAction.setX);
    lastX.set(id, this.x.getLong(id));
    this.x.set(id, x);
  }
  void setY(long y, int id){
    recordAction(id, y, EntityComponentAction.setY);
    lastY.set(id, this.y.getLong(id));
    this.y.set(id, y);
  }
  void setDirection(long direction, int id){
    recordAction(id, direction, EntityComponentAction.setDirection);
    lastDirection.set(id, this.direction.getLong(id));
    this.direction.set(id, direction);
  }
  void select(int id){
    if (isSelected.getBoolean(id))return;
    recordAction(id, id, EntityComponentAction.select);
    isSelected.set(id, true);
    selected.add(id);
  }
  void deselect(){
    globalActionBuffer.add(EntityComponentGlobalAction.deselect);
    for (int id : selected){
      isSelected.set(id, false);
    }
    selected.clear();
  }
  void kill(int id){
    recordAction(id, id, EntityComponentAction.kill);
    isActive.set(id, false);
    activeId.remove(id);
    generation.set(id, generation.getInt(id)+1);
  }
  void changeHp(int id, int change, int expectedGeneration){
    
    if (generation.getInt(id) != expectedGeneration) return;
    recordAction(id, change, expectedGeneration, EntityComponentAction.changeHp);
    hp.set(id, hp.getLong(id) + change);
    if (hp.getLong(id) <= 0)kill(id);
  }
  void makeUnit(Entity unit){
    //TODO: change unit logic to read from json and make units first
  }
  long allocateId() {
    int id = inactiveId.iterator().nextInt();
    inactiveId.remove(id);
    activeId.add(id);
    isActive.set(id, true);
    
    tags.set(id, 0L);
    abilities.set(id, 0L);
    effects.set(id, 0L);
    
    long gen = generation.getInt(id);
    return (gen << 32) | (id & 0xFFFFFFFFL);
  }
  
  void setTag(int id, long tag){
    recordAction(id, tag, EntityComponentAction.setTag);
    tags.set(id, tag);
  }
  void addTag(int id, long tag){
    recordAction(id, tag, EntityComponentAction.addTag);
    tags.set(id, tags.getLong(id) | tag);
  }
  public boolean hasTag(int id, long tag) {
    return (tags.getLong(id) & tag) != 0;
  }
  
  void setAbility(int id, long ability){
    recordAction(id, ability, EntityComponentAction.setAbility);
    abilities.set(id, ability);
  }
  void addAbility(int id, long ability){
    recordAction(id, ability, EntityComponentAction.addAbility);
    abilities.set(id, abilities.getLong(id) | ability);
  }
  public boolean hasAbility(int id, long ability) {
    return (abilities.getLong(id) & ability) != 0;
  }
  
  void setEffect(int id, long effect){
    recordAction(id, effect, EntityComponentAction.setEffect);
    effects.set(id, effect);
  }
  public boolean hasEffect(int id, long effect) {
    return (effects.getLong(id) & effect) != 0;
  }
  
  public void removeEffect(int id, long effect) {
    recordAction(id, effect, EntityComponentAction.removeEffect);
    effects.set(id, effects.getLong(id) & ~effect);
  }
  
  public void addEffect(int id, long effectBits, int ticks) {
    recordAction(id, effectBits, ticks, EntityComponentAction.addEffect);
    effects.set(id, effects.getLong(id) | effectBits);
    
    effected.add(id);
    effectMask.add(effectBits);
    effectTimer.add(ticks);
  }
  
  public void tickEffect(){
    for (int i = effectTimer.size() - 1; i >= 0; i--) {
      int remaining = effectTimer.getInt(i) - 1;
      
      if (remaining <= 0) {
        int eId = effected.getInt(i);
        long bitMask = effectMask.getLong(i);
        
        removeEffect(eId, bitMask);
        
        int lastIndex = effectTimer.size() - 1;
        effected.set(i, effected.getInt(lastIndex));
        effectMask.set(i, effectMask.getLong(lastIndex));
        effectTimer.set(i, effectTimer.getInt(lastIndex));
        
        effected.removeInt(lastIndex);
        effectMask.removeLong(lastIndex);
        effectTimer.removeInt(lastIndex);
      } else {
        effectTimer.set(i, remaining);
      }
    }
  }
  public void clearActionBuffers() {
    actionId.clear();
    actionType.clear();
    actionValue1.clear();
    actionValue2.clear();
    globalActionBuffer.clear();
  }
  public void prep(){
    //sync
    clearVisible();
    clearNextInCell();
    tickEffect();
    clearActionBuffers();
  }
}
