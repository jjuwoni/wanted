package com.ex.coffee;

public class Machine {

    private boolean isOn;
    private int water = 500;
    private int bean = 100;
    private int washCount = 0;

    public void turnOn() {
        if(isOn) {
            System.out.println("----------이미 전원이 켜져 있습니다.");
        } else {
            this.isOn=true;
            System.out.println("----------전원이 켜졌습니다. 커피를 내릴 준비가 되었습니다.");
        }
    }

    public void brewing() {
        if(isOn) {
            if(this.water>=100 && this.bean>=20 && this.washCount <3) {
                System.out.println("----------커피를 내립니다.");
                this.water -= 100;
                System.out.println("-----남은 물 : " + (this.water) + "ml");
                this.bean -= 20;
                System.out.println("-----남은 원두 : " + (this.bean) + "ml");
                this.washCount++;
            } else if(this.water<100 || this.bean<20) {
                System.out.println("----------재료가 부족합니다. 재료를 채워주세요.");
            }
            if(this.washCount>=3) {
                System.out.println("----------커피 머신을 세척해주세요.");
            }
        } else {
            System.out.println("----------전원이 꺼져있습니다. 전원을 켜주세요.");
        }
    }

    public void reFill() {
        if(isOn) {
            this.water = 500;
            this.bean = 100;
            System.out.println("----------재료를 가득 채웠습니다.");
        } else {
            System.out.println("----------전원이 꺼져있습니다. 전원을 켜주세요.");
        }
    }

    public void washing() {
        this.washCount = 0;
        System.out.println("----------커피 머신을 세척했습니다.");
    }

    public void turnOff() {
        if(isOn) {
            this.isOn = false;
            System.out.println("----------커피 머신의 전원을 끕니다.");
        } else {
            System.out.println("----------커피 머신은 이미 꺼져있습니다.");
        }

    }
}
