<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">
        <title>Spring Boot Email using FreeMarker</title>
        <style>
                body {
                    font-family: 'Arial', sans-serif;
                    background-color: #f4f4f4;
                    color: #333;
                    margin: 0;
                    padding: 0;
                    text-align: center;
                }

                h1 {
                    color: #007BFF;
                }

                .container {
                    background-color: #fff;
                    border-radius: 8px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                    margin: 20px auto;
                    max-width: 600px;
                    padding: 20px;
                }

                p {
                    line-height: 1.6;
                }

                a {
                    color: #007BFF;
                    text-decoration: none;
                    font-weight: bold;
                }

                a:hover {
                    text-decoration: underline;
                }

                .footer {
                    margin-top: 20px;
                    color: #555;
                }
        </style>
    </head>
    <body>
        <div class="container">

                <h1>Registration Verification</h1>

                <p>Dear ${account.username} ,</p>

                <p>Thank you for registering with our website. To complete your registration, please click the link below to verify your email address:</p>

                <p><a href="${verifyURL}">${verifyURL}</a></p>

                <h3>Your One-Time Password (OTP): ${otp}</h3>

                <p>If you didn't register on our website, please ignore this email.</p>

                <p class="footer">Best regards,<br>Your Website Team</p>

        </div>
    </body>
</html>