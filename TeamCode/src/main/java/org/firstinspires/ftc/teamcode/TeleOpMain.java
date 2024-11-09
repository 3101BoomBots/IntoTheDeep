package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends LinearOpMode{
    Hardware hw = Hardware.getInstance(this);
    final int SLIDES_INCREMENT = 25;
    final int MAX_SLIDES_POS = 1000;
    final int PIVOT_INCREMENT = 80;
    final int MAX_PIVOT_POS = 4560;
    final double intakeIncrement = 0.05;
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);

        waitForStart();

        double intakePower = 0.0;
        while (opModeIsActive()) {
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1.0);

            hw.frontLeft.setPower((drive + strafe + turn) / maxPower);
            hw.backLeft.setPower((drive - strafe + turn) / maxPower);
            hw.frontRight.setPower((drive - strafe - turn) / maxPower);
            hw.backRight.setPower((drive + strafe - turn) / maxPower);

            hw.slidesPushMotor.setPower(1);
            hw.slidesPivotMotor.setPower(1);

            double slides = gamepad2.left_stick_y;
            double pivot = gamepad2.right_stick_y;

            hw.slidesPushMotor.setTargetPosition((int) (hw.slidesPushMotor.getTargetPosition() - (slides * SLIDES_INCREMENT)));
            if(hw.slidesPushMotor.getTargetPosition() < 0) hw.slidesPushMotor.setTargetPosition(0);
            if(hw.slidesPushMotor.getTargetPosition() > MAX_SLIDES_POS) hw.slidesPushMotor.setTargetPosition(MAX_SLIDES_POS);

            hw.slidesPivotMotor.setTargetPosition((int) (hw.slidesPivotMotor.getTargetPosition() - (pivot * PIVOT_INCREMENT)));
            if(hw.slidesPivotMotor.getTargetPosition() < 0) hw.slidesPivotMotor.setTargetPosition(0);
            if(hw.slidesPivotMotor.getTargetPosition() > MAX_PIVOT_POS) hw.slidesPivotMotor.setTargetPosition(MAX_PIVOT_POS);

            if(gamepad2.a) {
                hw.slidesPushMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hw.slidesPushMotor.setTargetPosition(0);
                hw.slidesPushMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hw.slidesPushMotor.setPower(1);
            }
            if(gamepad2.b) {
                hw.slidesPivotMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                hw.slidesPivotMotor.setTargetPosition(0);
                hw.slidesPivotMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                hw.slidesPivotMotor.setPower(1);
            }

            if(gamepad1.dpad_up) intakePower += intakeIncrement;
            if(gamepad1.dpad_down) intakePower -= intakeIncrement;
            if(gamepad1.dpad_left) intakePower = 0;
            hw.intakeServo.setPower(intakePower);
            hw.telemetryHardware();
        }
    }
}
