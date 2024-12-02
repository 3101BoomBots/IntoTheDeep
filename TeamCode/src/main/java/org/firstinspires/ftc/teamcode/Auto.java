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
        while(opModeIsActive()){
            drive(12);
            drive(-12);
            strafe(12);
            strafe(-12);
            turn(90);
            turn(-90);
        }
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
        hw.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hw.backRight.setPower(minPower);
        hw.frontRight.setPower(minPower);
        hw.backLeft.setPower(-minPower);
        hw.frontLeft.setPower(-minPower);
        double error = degrees - hw.getGyroAngle();
        while(Math.abs(error) > threshold) {}
        hw.setMotorsToPower(0);
        hw.setMotorsToRunToPosition();
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
