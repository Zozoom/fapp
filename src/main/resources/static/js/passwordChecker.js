var myInput;
var myInput2;
var letter;
var capital;
var number;
var length;
var samePass;

window.onload = function () {
    myInput = document.getElementById("userPassword");
    myInput2 = document.getElementById("userPassword2");
    letter = document.getElementById("letter");
    capital = document.getElementById("capital");
    number = document.getElementById("number");
    length = document.getElementById("length");
    samePass = document.getElementById("samePass");

    myInput2.onkeyup = function () {
        // Validate SamePass
        if (myInput2.value === myInput.value) {
            samePass.classList.remove("invalid");
            samePass.classList.add("valid");
        } else {
            samePass.classList.remove("valid");
            samePass.classList.add("invalid");
        }
    }

    // When the user starts to type something inside the password field
    myInput.onkeyup = function () {
        // Validate lowercase letters
        var lowerCaseLetters = /[a-z]/g;
        if (myInput.value.match(lowerCaseLetters)) {
            letter.classList.remove("invalid");
            letter.classList.add("valid");
        } else {
            letter.classList.remove("valid");
            letter.classList.add("invalid");
        }

        // Validate capital letters
        var upperCaseLetters = /[A-Z]/g;
        if (myInput.value.match(upperCaseLetters)) {
            capital.classList.remove("invalid");
            capital.classList.add("valid");
        } else {
            capital.classList.remove("valid");
            capital.classList.add("invalid");
        }

        // Validate numbers
        var numbers = /[0-9]/g;
        if (myInput.value.match(numbers)) {
            number.classList.remove("invalid");
            number.classList.add("valid");
        } else {
            number.classList.remove("valid");
            number.classList.add("invalid");
        }

        // Validate length
        if (myInput.value.length >= 8) {
            length.classList.remove("invalid");
            length.classList.add("valid");
        } else {
            length.classList.remove("valid");
            length.classList.add("invalid");
        }

        // Validate SamePass
        if (myInput2.value === myInput.value) {
            samePass.classList.remove("invalid");
            samePass.classList.add("valid");
        } else {
            samePass.classList.remove("valid");
            samePass.classList.add("invalid");
        }
    }
};