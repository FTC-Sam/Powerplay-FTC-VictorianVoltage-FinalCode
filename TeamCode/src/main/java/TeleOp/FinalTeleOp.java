package TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "FinalTeleOp")
public class FinalTeleOp extends OpMode {

    //mecanum constants
    private DcMotorEx motorFrontLeft, motorBackLeft, motorFrontRight, motorBackRight;

    private double getMax(double[] input) {
        double max = Integer.MIN_VALUE;
        for (double value : input) {
            if (Math.abs(value) > max) {
                max = Math.abs(value);
            }
        }
        return max;
    }

    //servo constants
    private Servo rightServo;
    private Servo leftServo;

    //slide constants
    private Motor liftA;
    private Motor liftB;


    private double targetPos;


    //PID slides constants
    DcMotorEx pulleyMotorR;
    DcMotorEx pulleyMotorL;

    ElapsedTime timer = new ElapsedTime();

    private double lastError = 0;
    private double integralSum =0;

    public static double Kp =0.0125;
    public static double Ki =0.0; //.00005
    public static double Kd =0.0;


    public static double targetPosition = 5;

    private final FtcDashboard dashboard = FtcDashboard.getInstance();


    @Override
    public void init() {
        //motor initialization
        motorFrontLeft = (DcMotorEx) hardwareMap.dcMotor.get("FL");
        motorBackLeft = (DcMotorEx) hardwareMap.dcMotor.get("BL");
        motorFrontRight = (DcMotorEx) hardwareMap.dcMotor.get("FR");
        motorBackRight = (DcMotorEx) hardwareMap.dcMotor.get("BR");

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        //servo initialization

        rightServo = hardwareMap.get(Servo.class, "rightServo");
        leftServo = hardwareMap.get(Servo.class, "leftServo");



        //PID initialization
        dashboard.setTelemetryTransmissionInterval(25);
        pulleyMotorL = hardwareMap.get(DcMotorEx.class, "LeftSlideMotor");
        pulleyMotorR = hardwareMap.get(DcMotorEx.class, "RightSlideMotor");
        pulleyMotorR.setDirection(DcMotorSimple.Direction.REVERSE);

        pulleyMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pulleyMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        pulleyMotorL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pulleyMotorR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pulleyMotorL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pulleyMotorR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        targetPosition = 0;

    }

    @Override
    public void loop() {
        // mecanum
        boolean precisionToggle = gamepad1.right_trigger > 0.1;
        drive(precisionToggle);

        //servos
        if(gamepad2.b)
        {
            rightServo.setPosition(0.5);
            leftServo.setPosition(0.5);
        }
        if(gamepad2.a)
        {
            rightServo.setPosition(0.25);
            leftServo.setPosition(0.75);
        }



        //PID
        if (gamepad2.right_bumper && pulleyMotorL.getCurrentPosition() < 5200) {


            TelemetryPacket packet = new TelemetryPacket();
            double power = returnPower(targetPosition, pulleyMotorL.getCurrentPosition());
            packet.put("power", power);
            packet.put("position", pulleyMotorL.getCurrentPosition());
            packet.put("error", lastError);
            telemetry.addData("positon", pulleyMotorR.getCurrentPosition());
            telemetry.addData("positon", pulleyMotorL.getCurrentPosition());
            telemetry.addData("targetPosition", targetPosition);
            telemetry.addData("power", power);


            pulleyMotorL.setPower(power);
            pulleyMotorR.setPower(power);

            dashboard.sendTelemetryPacket(packet);

            targetPosition = targetPosition + 90;
        }
        else if(!gamepad2.right_bumper && !gamepad2.left_bumper) {
            pulleyMotorL.setPower(0);
            pulleyMotorR.setPower(0);
            targetPosition = pulleyMotorL.getCurrentPosition();


        }

        if(gamepad2.left_bumper && pulleyMotorL.getCurrentPosition() > 275) {

            double power = returnPower(targetPosition, pulleyMotorL.getCurrentPosition());
            telemetry.addData("positon", pulleyMotorR.getCurrentPosition());
            telemetry.addData("positon", pulleyMotorL.getCurrentPosition());
            telemetry.addData("targetPosition", targetPosition);
            telemetry.addData("power", power);


            pulleyMotorL.setPower(power);
            pulleyMotorR.setPower(power);



            targetPosition = targetPosition - 90;
        }
    }

    //mecanum methods
    public void drive(boolean precisionToggle) {
        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

        // Calculate the mecanum motor powers
        double frontLeftPower = (y + x + 2 * rx) / denominator;
        double backLeftPower = (y - x + 2 * rx) / denominator;
        double frontRightPower = (y - x - 2 * rx) / denominator;
        double backRightPower = (y + x - 2 * rx) / denominator;


        // Cube the motor powers
        frontLeftPower = Math.pow(frontLeftPower, 3);
        frontRightPower = Math.pow(frontRightPower, 3);
        backLeftPower = Math.pow(backLeftPower, 3);
        backRightPower = Math.pow(backRightPower, 3);

        // Calculate the maximum value of all the motor powers
        // The argument here is just an array separated into different lines
        double maxValue = getMax(new double[]{
                frontLeftPower,
                frontRightPower,
                backLeftPower,
                backRightPower
        });

        // Resize the motor power values
        if (maxValue > 1) {
            frontLeftPower /= maxValue;
            frontRightPower /= maxValue;
            backLeftPower /= maxValue;
            backRightPower /= maxValue;
        }

        if (precisionToggle) {
            motorFrontLeft.setPower(frontLeftPower * 0.6);
            motorBackLeft.setPower(backLeftPower * 0.6);
            motorFrontRight.setPower(frontRightPower * 0.6);
            motorBackRight.setPower(backRightPower * 0.6);
        } else {
            motorFrontLeft.setPower(frontLeftPower);
            motorBackLeft.setPower(backLeftPower);
            motorFrontRight.setPower(frontRightPower);
            motorBackRight.setPower(backRightPower);
        }

    }

    //PID methods
    public double returnPower(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error -lastError)/ timer.seconds();
        lastError = error;

        timer.reset();

        double output = (error * Kp) + (derivative * Kd) + (integralSum * Ki);
        return output; //figure out how to connect dashboard

    }

}