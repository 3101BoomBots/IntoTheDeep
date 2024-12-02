package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Hardware {
    public DcMotor frontLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backRight = null;
    public DcMotor backLeft = null;
    public DcMotor slidesPushMotor = null;
    public DcMotor slidesPivotMotor = null;
    public DcMotor hangingMotor = null;
    public CRServo intakeServo = null;  // CRServo = continuous servo
    private IMU gyro = null;
    private final OpMode opMode;
    private static Hardware myInstance;

    private Hardware(OpMode opMode) {
        this.opMode = opMode;
    }

    public static Hardware getInstance(OpMode opMode){
        if(myInstance == null){
            myInstance = new Hardware(opMode);
        }
        return myInstance;
    }

    public void init(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.dcMotor.get("leftFront");
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontRight = hardwareMap.dcMotor.get("rightFront");
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        backRight = hardwareMap.dcMotor.get("rightRear");
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        backLeft = hardwareMap.dcMotor.get("leftRear");
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        opMode.telemetry.addData("BackLeftMotor: ", "Initialized.");

        slidesPushMotor = hardwareMap.dcMotor.get("slides");
        slidesPushMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesPushMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        slidesPushMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        slidesPivotMotor = hardwareMap.dcMotor.get("pivot");
        slidesPivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slidesPivotMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hangingMotor = hardwareMap.dcMotor.get("hanging");
        hangingMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        hangingMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeServo = hardwareMap.crservo.get("intake");

        gyro = hardwareMap.get(IMU.class, "imu");
        // if vertically positioned
        gyro.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));

        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        slidesPushMotor.setTargetPosition(0);
        slidesPushMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slidesPivotMotor.setTargetPosition(0);
        slidesPivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    /**
     * @return angle - left is ___, right is ___
     */
    public double getGyroAngle() {
        return gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void setMotorsToPower(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    public void setTargets(int targetPos) {
        frontLeft.setTargetPosition(targetPos);
        frontRight.setTargetPosition(targetPos);
        backLeft.setTargetPosition(targetPos);
        backRight.setTargetPosition(targetPos);
    }

    public boolean notInRange() {
        return (notInRange(frontLeft.getTargetPosition()) &&
                notInRange(frontRight.getTargetPosition()) &&
                notInRange(backLeft.getTargetPosition()) &&
                notInRange(backRight.getTargetPosition())
        );
    }

    public boolean notInRange(int targetPos) {
        return (notInRange(frontLeft, targetPos, 10) && notInRange(frontRight, targetPos, 10)
                && notInRange(backLeft, targetPos, 10) && notInRange(backRight, targetPos, 10));
    }

    public static boolean notInRange(DcMotor motor, int targetPos, int threshold) {
        return !(((Math.abs(motor.getCurrentPosition()) - threshold) <= Math.abs(targetPos)) &&
                ((Math.abs(motor.getCurrentPosition() + threshold)) >= Math.abs(targetPos)));
    }

    public void telemetryHardware() {
        opMode.telemetry.addData("Motor Powers", "");
        opMode.telemetry.addData("FrontLeftPower: ", frontLeft.getPower());
        opMode.telemetry.addData("FrontRightPower: ", frontRight.getPower());
        opMode.telemetry.addData("backRightPower: ", backRight.getPower());
        opMode.telemetry.addData("backLeftPower: ", backLeft.getPower());
        opMode.telemetry.addData("FrontLeftPosition: ", frontLeft.getCurrentPosition());
        opMode.telemetry.addData("FrontRightPosition: ", frontRight.getCurrentPosition());
        opMode.telemetry.addData("backRightPosition: ", backRight.getCurrentPosition());
        opMode.telemetry.addData("backLeftPosition: ", backLeft.getCurrentPosition());
        opMode.telemetry.addData("Arm", "");
        opMode.telemetry.addData("Slides Motor: ", slidesPushMotor.getCurrentPosition());
        opMode.telemetry.addData("Slides Motor Target: ", slidesPushMotor.getTargetPosition());
        opMode.telemetry.addData("Pivot Motor: ", slidesPivotMotor.getCurrentPosition());
        opMode.telemetry.addData("Pivot Motor Target: ", slidesPivotMotor.getTargetPosition());
        opMode.telemetry.addData("Intake Servo: ", intakeServo.getPower());
        opMode.telemetry.addData("IMU Angle: ", getGyroAngle());
    }

    public void setMotorsToRunToPosition() {
        setTargets(0);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}
