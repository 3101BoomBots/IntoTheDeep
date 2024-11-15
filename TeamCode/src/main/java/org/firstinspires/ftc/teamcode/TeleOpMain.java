package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "TeleOpMain")
public class TeleOpMain extends LinearOpMode{
    Hardware hw = Hardware.getInstance(this);

    double strafe;
    double drive;
    double turn;

    final int SLIDES_INCREMENT = 65;
    final int MAX_SLIDES_POS = 1675;
    final int PIVOT_INCREMENT = 80;
    final int MAX_PIVOT_POS = 5300;  // MAX - 90deg (perpendicular to robot)  = 5633
    final double INTAKE_MAX_POWER = 1;
    final int HIGH_MAX_SLIDES = 2250;
    // 5163 PIVOT, 1971 Slides high basket
    // Straight ahead -2500
    final double STRAIGHT_PIVOT = 2600;  // little more for safety
    @Override
    public void runOpMode() throws InterruptedException {
        hw.init(hardwareMap);

        waitForStart();

        double intakePower = 0.0;
        while (opModeIsActive()) {
            drive = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            turn = gamepad1.right_stick_x;

            boolean dpadUpClicked = gamepad1.dpad_up;
            boolean dpadLeftClicked = gamepad1.dpad_left;
            boolean dpadRightClicked = gamepad1.dpad_right;
            boolean dpadDownClicked = gamepad1.dpad_down;

            if (dpadUpClicked || dpadRightClicked || dpadLeftClicked || dpadDownClicked) resetControls();
            if(dpadUpClicked) drive = 0.5;
            if(dpadDownClicked) drive = -0.5;
            if(dpadLeftClicked) strafe = -0.5;
            if(dpadRightClicked) strafe = 0.5;

            double maxPower = Math.max(Math.abs(drive) + Math.abs(turn) + Math.abs(strafe), 1.0);

            hw.frontLeft.setPower((drive + strafe + turn) / maxPower);
            hw.backLeft.setPower((drive - strafe + turn) / maxPower);
            hw.frontRight.setPower((drive - strafe - turn) / maxPower);
            hw.backRight.setPower((drive + strafe - turn) / maxPower);

            hw.slidesPushMotor.setPower(1);
            hw.slidesPivotMotor.setPower(1);

            double slides = gamepad2.right_stick_y;
            double pivot = gamepad2.left_stick_y;

            hw.slidesPushMotor.setTargetPosition((int) (hw.slidesPushMotor.getTargetPosition() - (slides * SLIDES_INCREMENT)));
            if(hw.slidesPushMotor.getTargetPosition() < 0) hw.slidesPushMotor.setTargetPosition(0);
            if(hw.slidesPivotMotor.getTargetPosition() > STRAIGHT_PIVOT + 200 || hw.slidesPivotMotor.getTargetPosition() < 2200) {
                if(hw.slidesPushMotor.getTargetPosition() > HIGH_MAX_SLIDES) hw.slidesPushMotor.setTargetPosition(HIGH_MAX_SLIDES);
            } else if(hw.slidesPushMotor.getTargetPosition() > MAX_SLIDES_POS) hw.slidesPushMotor.setTargetPosition(MAX_SLIDES_POS);

            hw.slidesPivotMotor.setTargetPosition((int) (hw.slidesPivotMotor.getTargetPosition() - (pivot * PIVOT_INCREMENT)));
            if(hw.slidesPivotMotor.getTargetPosition() < 0) hw.slidesPivotMotor.setTargetPosition(0);
            if(hw.slidesPivotMotor.getTargetPosition() > MAX_PIVOT_POS) hw.slidesPivotMotor.setTargetPosition(MAX_PIVOT_POS);

            if(gamepad2.dpad_up) intakePower = INTAKE_MAX_POWER;
            if(gamepad2.dpad_down) intakePower = -INTAKE_MAX_POWER;
            if(gamepad2.dpad_left) intakePower = 0;


            hw.intakeServo.setPower(intakePower);
            hw.telemetryHardware();
        }
    }

    private void resetControls() {
        this.drive = 0;
        this.strafe = 0;
        this.turn = 0;
    }
}
