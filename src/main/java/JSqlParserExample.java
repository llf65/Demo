import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

public class JSqlParserExample {
    public static void main(String[] args) {
        String sql = "SELECT count(1)  from table";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

        if (statement instanceof Select) {
            Select selectStatement = (Select) statement;
            SelectBody selectBody = selectStatement.getSelectBody();
            if (parseSelectBody(selectBody)) {
                countFound();
                System.out.println(sql);
            } else {
                String newSql = PagerUtils.limit(sql, DbType.oracle, 0, 10);
                System.out.println(newSql);
            }
        }
    }

    private static void countFound() {
        // Your logic when COUNT function is found
        System.out.println("COUNT function is found!");
    }

    private static void countNotFound() {
        // Your logic when COUNT function is not found
        System.out.println("COUNT function is not found!");
    }

    private static boolean parseSelectBody(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
            for (SelectItem selectItem : selectItems) {
                if (selectItem.toString().toUpperCase().contains("COUNT")) {
                    return true;
                }
            }
        } else if (selectBody instanceof SetOperationList) {
            List<SelectBody> selectBodies = ((SetOperationList) selectBody).getSelects();
            for (SelectBody body : selectBodies) {
                if (parseSelectBody(body)) {
                    return true;
                }
            }
        } else if (selectBody instanceof WithItem) {
            SelectBody withItemSelectBody = ((WithItem) selectBody).getSelectBody();
            if (parseSelectBody(withItemSelectBody)) {
                return true;
            }
        }
        return false;
    }
}
