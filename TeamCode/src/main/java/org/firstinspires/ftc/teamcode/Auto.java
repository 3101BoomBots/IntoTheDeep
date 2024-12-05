package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "AutoMain")
public class Auto extends LinearOpMode{
    static final double TICKS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 5.512;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    final private double DEFAULT_POWER = 1.0;
    Hardware hw = Hardware.getInstance(this);
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        ElapsedTime elapsedTime = new ElapsedTime();
        hw.setMotorsToRunToPosition();
        waitForStart();
        while(elapsedTime.seconds() < 5) hw.intakeServo.setPower(-1);
        hw.slidesPivotMotor.setTargetPosition(200);
        drive(20);
        turn(-90);
        drive(40);
        turn(-45);
        hw.slidesPivotMotor.setTargetPosition(4460);
        hw.slidesPushMotor.setTargetPosition(2222);
        while(opModeIsActive()) {
            hw.telemetryHardware();
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

        while(opModeIsActive() && hw.notInRange()) hw.telemetryHardware();
        hw.setMotorsToPower(0);
    }

    /**
     * @param degrees positive is left, right negative
     */
    private void turn(double degrees) {
        turn(degrees, degrees > 0 ? 0.7 : -0.7, 0.50);
    }

    private void turn(double degrees, double power, double threshold) {
        degrees = AngleUnit.normalizeDegrees(degrees);
        double error = threshold + 1;
        hw.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hw.backRight.setPower(power);
        hw.frontRight.setPower(power);
        hw.backLeft.setPower(-power);
        hw.frontLeft.setPower(-power);
        while(Math.abs(error) > threshold && opModeIsActive()) {
            error = degrees - AngleUnit.normalizeDegrees(Math.abs(hw.getGyroAngle()));
            hw.telemetryHardware();
        }
        hw.setMotorsToPower(0);
        hw.setMotorsToRunToPosition();
        hw.gyro.resetYaw();
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

        while(opModeIsActive() && hw.notInRange()) hw.telemetryHardware();
        hw.setMotorsToPower(0);
    }
}
