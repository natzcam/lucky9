/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nac.lucky9.game;

/**
 *
 * @author Administrator
 */
public class Card {
  
  private Suit suit;
  private Type type;
  private boolean faceUp = false;
  
  public Card(Suit suit, Type type) {
    this.suit = suit;
    this.type = type;
  }
  
  public void setFaceUp(boolean faceUp) {
    this.faceUp = faceUp;
  }
  
  public boolean isFaceUp() {
    return faceUp;
  }
  
  public Type getType() {
    return type;
  }
  
  public Suit getSuit() {
    return suit;
  }
  
  @Override
  public String toString() {
    return (type + "-" + suit).toLowerCase();
  }
  
  public String toClass(Boolean showViewer) {
    if (faceUp || showViewer) {
      return (type + "-" + suit).toLowerCase();
    } else {
      return "card-back";
    }
  }
  
  public static enum Type {
    
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(0),
    JACK(0),
    QUEEN(0),
    KING(0);
    private final int value;
    
    private Type(int value) {
      this.value = value;
    }
    
    public int value() {
      return value;
    }
  }
  
  public static enum Suit {
    
    HEARTS,
    CLUBS,
    SPADES,
    DIAMONDS
  }
}
