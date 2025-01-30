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
    static final double WHEEL_DIAMETER_INCHES = 3.75 * ((double)25/24);
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    final private double DEFAULT_POWER = 0.5;

    final private int DISTANCE_TO_BASKET = 6;
    final private int DEGREES_BASKET = 15;
    Hardware hw = Hardware.getInstance(this);
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);
        hw.setMotorsToRunToPosition();
        waitForStart();
        hw.slidesPivotMotor.setPower(0.7);
        hw.slidesPushMotor.setPower(0.7);
        hw.intakeServo.setPower(-1);
        sleep(5000);
        hw.slidesPushMotor.setTargetPosition(200);
        while(hw.slidesPushMotor.isBusy());
        hw.slidesPivotMotor.setTargetPosition(1200);
        strafe(19);
        drive(DISTANCE_TO_BASKET);
        hw.slidesPivotMotor.setTargetPosition(4800);
        while(hw.slidesPivotMotor.isBusy());
        hw.slidesPushMotor.setTargetPosition(2950);
        while(hw.slidesPushMotor.isBusy());
        turn(DEGREES_BASKET);
        sleep(2000);
        hw.intakeServo.setPower(0.5);
        sleep(2000);
        hw.intakeServo.setPower(0);
        turn(-DEGREES_BASKET);
        sleep(2000);
        hw.slidesPivotMotor.setTargetPosition(4830);
        sleep(250);
        hw.slidesPushMotor.setTargetPosition(100);
        hw.slidesPivotMotor.setTargetPosition(400);
        drive(-15);
        strafe(68);
        drive(-33);
        hw.hangingMotor.setPower(1);
        sleep(1750);
        hw.hangingMotor.setPower(0);
        // red facing basket
        hw.telemetryHardware();
    }

    private void drive(double inches) {
        drive(inches, DEFAULT_POWER);
    }

    private void drive(double inches, double power) {
        int target = (int) (inches * TICKS_PER_INCH);
//        int target = (int) (inches * TICKS_PER_INCH);

        hw.frontRight.setTargetPosition(hw.frontRight.getCurrentPosition() + target);
        hw.frontLeft.setTargetPosition(hw.frontLeft.getCurrentPosition() + target);
        hw.backRight.setTargetPosition(hw.backRight.getCurrentPosition() + target);
        hw.backLeft.setTargetPosition(hw.backLeft.getCurrentPosition() + target);

        hw.setMotorsToPower(power);

        while(opModeIsActive() && hw.motorsBusy()) hw.telemetryHardware();
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
            error = degrees - AngleUnit.normalizeDegrees(hw.getGyroAngle());
//            telemetry.addData("Error", error);
//            telemetry.update();
//            hw.telemetryHardware();
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

        hw.frontLeft.setTargetPosition(hw.frontLeft.getTargetPosition() + target);
        hw.frontRight.setTargetPosition(hw.frontRight.getTargetPosition() - target);
        hw.backLeft.setTargetPosition(hw.backLeft.getTargetPosition() - target);
        hw.backRight.setTargetPosition(hw.backRight.getTargetPosition() + target);
        hw.setMotorsToPower(power);

        while(opModeIsActive() && hw.motorsBusy()) hw.telemetryHardware();
        hw.setMotorsToPower(0);
    }
}
