package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.follower.Follower;
import org.firstinspires.ftc.teamcode.pedroPathing.localization.Pose;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierCurve;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.BezierLine;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.PathChain;
import org.firstinspires.ftc.teamcode.pedroPathing.pathGeneration.Point;
import org.firstinspires.ftc.teamcode.pedroPathing.util.Timer;

@Autonomous(name="pedroAuto")
public class PedroAuto extends OpMode {
    private PathChain preloadPath, basketPath, sampleOnePath, sampleOneScore, sampleTwoPath,
            sampleTwoScore, sampleThreePath, sampleThreeScore, backBasketPath;
    Timer pathTimer;
    private Follower robot;
    private PedroHardware hw;
    private final int INTAKE_PUSH = 140;
    private final int RAISED_INTAKE = 785;
    private final int INTAKE_PIVOT = 85;
    private final int BASKET_PIVOT = 5115;
    private final int BASKET_PUSH = 3360;
    int state = 0;

//    enum FieldSide {RED, BLUE};

    private Pose buildPaths() {
        Pose initialPos = new Pose(0, 0, Math.toRadians(0));
        Pose frontOfBasket = new Pose(13, -9, Math.toRadians(45));
        Pose basketForward = new Pose(15, -7, Math.toRadians(45));  // moves forward
        // into the basket after the arm is up
        Pose firstSample = new Pose(0, -38, Math.toRadians(0));
        Pose secondSample = new Pose(10, -38, Math.toRadians(0));
        Pose thirdSample = new Pose(20, -38, Math.toRadians(0));

        preloadPath = linearPathChain(initialPos, frontOfBasket);

        basketPath = linearPathChain(frontOfBasket, basketForward);

        backBasketPath = linearPathChain(basketForward, frontOfBasket);

        // Bezier Curve first point is start point, points in the middle are control points, last is end point
        sampleOnePath = robot.pathBuilder()
                .addPath(new BezierCurve(new Point(frontOfBasket), new Point(
                        firstSample.getX() - 18, firstSample.getY() - 11), new Point(firstSample)
                ))
                .setLinearHeadingInterpolation(frontOfBasket.getHeading(), firstSample.getHeading())
                .build();

        sampleOneScore = linearPathChain(firstSample, frontOfBasket);

        sampleTwoPath = robot.pathBuilder()
                .addPath(new BezierCurve(new Point(frontOfBasket),
                        new Point(secondSample.getX() - 18, secondSample.getY() - 11),
                        new Point(secondSample)))
                .setLinearHeadingInterpolation(frontOfBasket.getHeading(), secondSample.getHeading())
                .build();

        sampleTwoScore = linearPathChain(secondSample, frontOfBasket);

        sampleThreePath = robot.pathBuilder()
                .addPath(new BezierCurve(new Point(frontOfBasket),
                        new Point(thirdSample.getX() - 18, thirdSample.getY() - 11),
                        new Point(thirdSample)))
                .setLinearHeadingInterpolation(frontOfBasket.getHeading(), thirdSample.getHeading())
                .build();

        sampleThreeScore = linearPathChain(thirdSample, frontOfBasket);

        return initialPos;
    }

    private void autonomousPathUpdate() {
        switch (state) {
            case 0: // Move from start to scoring position
                hw.intakeServo.setPower(-1);  // intake servo is a CRServo meaning it can run using power continuously
                robot.followPath(preloadPath, true); // works
                hw.slidesPivotMotor.setTargetPosition(BASKET_PIVOT);
                changeState(1);
                break;
            case 1:
                if (!hw.slidesPivotMotor.isBusy()) {  // once the arm is up, push it out
                    hw.slidesPushMotor.setTargetPosition(BASKET_PUSH);
                    changeState(201);
                }
                break;
            case 201:
                if (!robot.isBusy()) {  // wait until preload path is finished before following this path
                    robot.followPath(basketPath, true);
                    changeState(2);
                }
                break;
            case 2:
                if(!hw.slidesPushMotor.isBusy() && !hw.slidesPivotMotor.isBusy() && !robot.isBusy()) {
                    pathTimer.resetTimer();
                    hw.intakeServo.setPower(1); // outtake
                    while(pathTimer.getElapsedTimeSeconds() < 1.5);  // time to outtake preload sample
                    hw.intakeServo.setPower(0);
                    changeState(301);
                }
                break;
            case 301:
                if (!robot.isBusy()) {
                    robot.followPath(backBasketPath, true);
                    changeState(3);
                }
                break;
            case 3:  // preload finished
                if(!robot.isBusy()) {
                    robot.followPath(sampleOnePath, true);
                    changeState(4);
                }
                break;
            case 4:
                hw.slidesPushMotor.setTargetPosition(0);
                hw.slidesPivotMotor.setTargetPosition(RAISED_INTAKE);
                changeState(6);
                break;
            case 6:  // sample one intake
                intakeAtSample(7);
                break;
            case 7:
                hw.slidesPivotMotor.setTargetPosition(BASKET_PIVOT);
                robot.followPath(sampleOneScore, true);
                changeState(8);
                break;
            case 8:
                if (!hw.slidesPivotMotor.isBusy()) {
                    hw.slidesPushMotor.setTargetPosition(BASKET_PUSH);
                    changeState(901); // stops for now
                }
                break;
            case 901:
                if (!robot.isBusy()) {
                    robot.followPath(basketPath, true);
                    changeState(9);
                }
                break;
            case 9:
                if(!hw.slidesPushMotor.isBusy() && !hw.slidesPivotMotor.isBusy()) {
                    hw.intakeServo.setPower(1);
                    while (pathTimer.getElapsedTimeSeconds() < 1.5) ; // time to outtake
                    hw.intakeServo.setPower(0);
                    changeState(1010);
                }
                break;
            case 1010:
                if (!robot.isBusy()) {
                    robot.followPath(backBasketPath, true);
                    changeState(10);
                }
                break;
            case 10: // Sample one scored
                if(!robot.isBusy()) {
                    robot.followPath(sampleTwoPath, true);
                    changeState(11);
                }
                break;
            case 11:
                hw.slidesPushMotor.setTargetPosition(0);
                hw.slidesPivotMotor.setTargetPosition(RAISED_INTAKE);
                changeState(13);
                break;
            case 13:
                intakeAtSample(14);
                break;
            case 14:
                hw.slidesPivotMotor.setTargetPosition(BASKET_PIVOT);
                robot.followPath(sampleTwoScore, true);
                changeState(15);
                break;
            case 15:
                if (!hw.slidesPivotMotor.isBusy()) {
                    hw.slidesPushMotor.setTargetPosition(BASKET_PUSH);
                    changeState(16); // stops for now
                }
                break;
            case 16:
                if(!robot.isBusy()) {
                    robot.followPath(basketPath);
                    changeState(17);
                }
                break;
            case 17:
                hw.intakeServo.setPower(1);
                while (pathTimer.getElapsedTimeSeconds() < 1.5); // time to outtake
                hw.intakeServo.setPower(0);
                changeState(1801);
                break;
            case 1801:
                if (!robot.isBusy()) {
                    robot.followPath(backBasketPath);
                    changeState(18);
                }
                break;
            case 18: // Sample two scored
                if(!robot.isBusy()) {
                    robot.followPath(sampleThreePath, true);
                    changeState(19);
                }
                break;
            case 19:
                hw.slidesPushMotor.setTargetPosition(0);
                hw.slidesPivotMotor.setTargetPosition(RAISED_INTAKE);
                changeState(20);
                break;
            case 20:
                intakeAtSample(21);
                break;
            case 21:
                hw.slidesPivotMotor.setTargetPosition(BASKET_PIVOT);
                robot.followPath(sampleThreeScore, true);
                changeState(22);
                break;
            case 22:
                if (!hw.slidesPivotMotor.isBusy()) {
                    hw.slidesPushMotor.setTargetPosition(BASKET_PUSH);
                    changeState(23); // stops for now
                }
                break;
            case 23:
                if (!robot.isBusy()) {
                    robot.followPath(basketPath);
                    changeState(24);
                }
                break;
            case 24:
                if(!hw.slidesPivotMotor.isBusy() && !hw.slidesPushMotor.isBusy()) {
                    hw.intakeServo.setPower(1);
                    while (pathTimer.getElapsedTimeSeconds() < 1.5) ; // time to outtake
                    hw.intakeServo.setPower(0);
                    changeState(25);
                }
                break;
            case 25:
                if (!robot.isBusy()) {
                    robot.followPath(backBasketPath);
                    changeState(26);
                }
                break;
            case 26:
                if (!robot.isBusy()) {
                    state = -1;
                }
                break;
        }
    }

    private void changeState(int newState) {
        state = newState;
        pathTimer.resetTimer();
        telemetry.addData("Moving on to", newState);
    }

    private PathChain linearPathChain(Pose pose1, Pose pose2) {
        return robot.pathBuilder()
                .addPath(new BezierLine(new Point(pose1), new Point(pose2)))
                .setLinearHeadingInterpolation(pose1.getHeading(), pose2.getHeading())
                .build();
    }
    // Red = 90 degrees
    @Override
    public void init() {
        robot = new Follower(hardwareMap);
//        Pose initialPos = buildPaths(getInput(new FieldSide[]{FieldSide.RED, FieldSide.BLUE}));
        Pose initialPos = buildPaths();
        robot.setMaxPower(1);
        robot.setStartingPose(initialPos);
        pathTimer = new Timer();
        hw = PedroHardware.getInstance(this);
        hw.init(hardwareMap);
        hw.slidesPivotMotor.setPower(1);
        hw.slidesPushMotor.setPower(1);
    }
    @Override
    public void loop() {
        robot.update();
        autonomousPathUpdate();

//        telemetry.addData("Path State", state);
//        telemetry.addData("Position", robot.getPose().toString());
        robot.telemetryDebug(FtcDashboard.getInstance().getTelemetry());
        telemetry.setAutoClear(false);
        telemetry.update();
    }

    private void intakeAtSample(int newState) {
        if (robot.getCurrentPath().isAtParametricEnd()) {
            hw.slidesPushMotor.setTargetPosition(INTAKE_PUSH);
            hw.slidesPivotMotor.setTargetPosition(INTAKE_PIVOT);
            hw.intakeServo.setPower(-1);
            pathTimer.resetTimer();
            while (pathTimer.getElapsedTimeSeconds() < 2) ; // time to intake sample one
            changeState(newState);
        }
    }
    public <T> T getInput(T[] modes) {
        while (true) {
            if (gamepad1.dpad_left && modes[0] != null) {
                return modes[0];
            } else if (gamepad1.dpad_right && modes[1] != null) {
                return modes[1];
            } else if (gamepad1.dpad_up && modes[2] != null) {
                return modes[2];
            } else if (gamepad1.dpad_down && modes[3] != null) {
                return modes[3];
            }
        }
    }
}