package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class PedroHardware {
    public DcMotor slidesPushMotor = null;
    public DcMotor slidesPivotMotor = null;
    public DcMotor hangingMotor = null;
    public CRServo intakeServo = null;  // CRServo = continuous servo
    private final OpMode opMode;
    private static PedroHardware myInstance;

    private PedroHardware(OpMode opMode) {
        this.opMode = opMode;
    }

    public static PedroHardware getInstance(OpMode opMode){
        if(myInstance == null){
            myInstance = new PedroHardware(opMode);
        }
        return myInstance;
    }

    public void init(HardwareMap hardwareMap) {
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

        slidesPushMotor.setTargetPosition(0);
        slidesPushMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        slidesPivotMotor.setTargetPosition(0);
        slidesPivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }


    public void telemetryHardware() {
        opMode.telemetry.addData("Slides Motor: ", slidesPushMotor.getCurrentPosition());
        opMode.telemetry.addData("Slides Motor Target: ", slidesPushMotor.getTargetPosition());
        opMode.telemetry.addData("Pivot Motor: ", slidesPivotMotor.getCurrentPosition());
        opMode.telemetry.addData("Pivot Motor Target: ", slidesPivotMotor.getTargetPosition());
        opMode.telemetry.addData("Intake Servo: ", intakeServo.getPower());
        opMode.telemetry.update();
    }
}
