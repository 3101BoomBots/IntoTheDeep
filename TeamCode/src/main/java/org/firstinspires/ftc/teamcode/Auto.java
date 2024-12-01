package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutoMain")
public class Auto extends LinearOpMode{
    static final double TICKS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0; // needs to be changed
    static final double WHEEL_DIAMETER_INCHES = 5.512; // needs to be changed
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    final private double DEFAULT_POWER = 1.0;
    Hardware hw = Hardware.getInstance(this);
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        hw.setMotorsToRunToPosition();
        waitForStart();
        drive(12);
        drive(-12);
    }

    private void drive(double inches) {
        drive(inches, DEFAULT_POWER);
    }

    private void drive(double inches, double power) {
//        int target = (int) ((inches * TICKS_PER_INCH) + (9 * TICKS_PER_INCH));
        int target = (int) (inches * TICKS_PER_INCH);

        hw.setMotorsToPower(power);

        hw.frontRight.setTargetPosition(hw.frontRight.getTargetPosition() + target);
        hw.frontLeft.setTargetPosition(hw.frontLeft.getTargetPosition() + target);
        hw.backRight.setTargetPosition(hw.backLeft.getTargetPosition() + target);
        hw.backLeft.setTargetPosition(hw.backLeft.getTargetPosition() + target);

        while(opModeIsActive() && hw.notInRange());
        hw.setMotorsToPower(0);
    }

    private void turn(double degrees) {
        turn(degrees, degrees > 0 ? 0.15 : -0.15, 0.50);
    }

    private void turn(double degrees, double minPower, double threshold) {
    }

    /**
     @param inches - left is negative, right is positive
     */
    private void strafe(double inches) {
        strafe(inches, DEFAULT_POWER);
    }

    private void strafe(double inches, double power) {
        int target = (int) (inches * TICKS_PER_INCH);
        hw.setMotorsToPower(power);

        hw.frontLeft.setTargetPosition(hw.frontLeft.getTargetPosition() + target);
        hw.frontRight.setTargetPosition(hw.frontRight.getTargetPosition() - target);
        hw.backLeft.setTargetPosition(hw.backLeft.getTargetPosition() - target);
        hw.backRight.setTargetPosition(hw.backRight.getTargetPosition() + target);

        while(opModeIsActive() && hw.notInRange());
        hw.setMotorsToPower(0);
    }
}
