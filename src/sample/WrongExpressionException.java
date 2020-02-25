package sample;

public class WrongExpressionException extends Exception {

    public enum Type {
        INCORRECT_BRACKETS,
        WRONG_ARGS,
        WRONG_ARGS_QUAN,
        INCORRECT_NAME,
        INCORRECT_EQUALITY,
        OPERATOR_MISSED,
        DUPLICATE_VAR_ASSIGNMENT,
        OTHER
    }

    public Type type;

    public WrongExpressionException(String message) {
        super(message);
        this.type = Type.OTHER;
    }

    public WrongExpressionException(Type type) {
        super("");
        this.type = type;
    }

    public WrongExpressionException(Type type, String message) {
        super(message);
        this.type = type;
    }

    public String toString(String lang) {
        String answer = "";
        switch (type) {
            case INCORRECT_BRACKETS:
                answer = "Некорректные скобки";
                break;
            case WRONG_ARGS:
                answer = "Неверные аргументы ";
                break;
            case WRONG_ARGS_QUAN:
                answer = "Неверное количество аргументов ";
                break;
            case INCORRECT_NAME:
                answer = "Некорректное имя ";
                break;
            case INCORRECT_EQUALITY:
                answer = "Некорректное равенство ";
                break;
            case OPERATOR_MISSED:
                answer = "Пропущен оператор ";
                break;
            case DUPLICATE_VAR_ASSIGNMENT:
                answer = "Двойное присвоение переменной ";
                break;
            default:
                answer = "";
                break;
        }
        return answer + getMessage();
    }

    @Override
    public String toString() {
        return toString("EN");
    }
}
