包 piuk.merchant ;

進口 com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ;
進口 org.apache.commons.io.IOUtils ;
進口 org.json.simple.JSONValue ;
進口 piuk.db.BitcoinDatabaseManager ;
進口 piuk.website.BaseServlet ;

進口 javax.servlet.ServletException ;
進口 javax.servlet.annotation.WebServlet ;
進口 javax.servlet.http.HttpServletRequest ;
進口 javax.servlet.http.HttpServletResponse ;
進口 java.io.IOException的 ;
進口 java.lang.Exception的 ;
進口 java.lang.Integer中 ;
進口 java.net.HttpURLConnection中 ;
進口 java.net.InetAddress中 ;
進口 的java.net.URL ;
進口 java.net.URLEncoder中 ;
進口 java.sql.Connection中 ;
進口 java.sql.PreparedStatement中 ;
進口 的java.util.Map ;

@WebServlet（ “ / callback_handler “）
公共 類 CallbackHandlerServlet  擴展 BaseServlet {
    私人 靜態 最後 弦樂 ROOT  =  “ https://blockchain.info/ “ ;
    私人 靜態 最後 弦樂 CALLBACK_URL  =  “ https://mydomain.com/callback_handler “ ;
 私人 靜態 最後 弦樂 MY_BITCOIN_ADDRESS  = "1PfsocUPykG8nfQToVDAu9hS2fsA1d6Hdd“ ;

    私人 靜態 字符串 fetchURL（字符串 URL）拋出 異常 {
        URL網址=  新 網址（URL）;

        HttpURLConnection的連接=（HttpURLConnection類）URL 。的openConnection（）;

        連接。 setConnectTimeout（10000）;
        連接。 setReadTimeout（10000）;

        連接。 setInstanceFollowRedirects（假）;

        連接。連接（）;

        如果（連接。 getResponseCode（）！=  200）{
            拋出 新的 異常（“無效的HTTP響應代碼“  +連接。 getResponseCode（））;
        }

        返回 IOUtils 。的toString（連接。的getInputStream（）， “ UTF-8 “）;
    }

    / **
     *生成一個唯一的付款地址的用戶發送付款
     * @參數 myAddress您的比特幣地址
     * @參數回調收到付款時將通知回調URL
     * @參數匿名交易是否應該是匿名與否
     * @參數 PARAMS要傳遞給回調URL額外的參數
     * @返回
     * @throws異常
     * /
    public  static  String  generatePaymentAddress ( String  myAddress , String  callback , boolean  anonymous , Map< String , String >  params ) throws  Exception {
        String url =  ROOT  +   " api/receive?method=create&callback= " +  URLEncoder . encode(callback, " UTF-8 " ) + " &anonymous= " + anonymous + " &address= " + myAddress;

        //附加任何自定義參數回調
        對於（地圖。進入< 字符串，字符串 >參數： PARAMS 。的entrySet（））{
            網址+ =  “＆“ +參數。 getKey（）+ “ = “ + URLEncoder的。編碼（參數。的getValue（）， “ UTF-8 “）;
        }

        字符串響應= fetchURL（URL）;

        如果（響應==  空）
            拋出 新的 異常（“服務器返回NULL響應“）;

        地圖< 字符串，對象 > OBJ =（地圖< 字符串，對象 >）JSONValue 。解析（響應）;

        如果（OBJ 。獲得（“錯誤“）！=  空）
            拋出 新的 異常（（字符串）目標文件。獲得（“錯誤“））;

        回報（字符串）目標文件。獲得（“ input_address “）;
    }

    //轉換的當地貨幣金額為BTC
    //如convertToBTC（“USD”，1）返回1美元的BTC的價值
    公共 靜態 雙 convertToBTC（字符串 COUNTRYCODE，雙 量）拋出 異常 {
        字符串響應= fetchURL（ROOT  +   “？tobtc貨幣= “ + COUNTRYCODE + “＆值= “ +量）;

        如果（響應！=  空）
            返回 雙。的valueOf（響應）;
        其他
            返回 拋出 新的 異常（“未知響應“）;
    }

    @覆蓋
    保護 無效 的doGet（HttpServletRequest的 REQ，HttpServletResponse的 水庫）拋出 了ServletException，IOException異常 {
        字符串值= REQ 。的getParameter（ “值“）;
        字符串 transaction_hash = REQ 。的getParameter（ “ transaction_hash “）;
        字符串 GUID = REQ 。的getParameter（ “ GUID “）;

        布爾授權=  虛假的 ;

        // Chekc請求的IP地址匹配blockhain.info
        InetAddress類 [] IPS =  的InetAddress 。 getAllByName（ “ blockchain.info “）;
        對於（InetAddress類地址： IPS）{
            如果（REQ 。的getRemoteAddr（） 。等於（地址。 getHostAddress（）））{
                授權=  真正的 ;
                打破 ;
            }
        }

        如果（！授權）
            返回 ;

        連接康恩=  BitcoinDatabaseManager 。康恩（）;
        嘗試 {
             PreparedStatement的語句= CONN 。 prepareStatement（ “插入user_deposits（tx_hash，GUID值）值（？，）？ “）;
             嘗試 {
                 語句。了setString（1，transaction_hash）;
                 語句。了setString（2，GUID）;
                 語句。 setLong（3，龍。的valueOf（值））;

                 如果（語句。的executeUpdate（）==  1）{
                    RES 。的getOutputStream（） 。打印（“ * OK * “）;
                 }
             } 終於 {
                 BitcoinDatabaseManager 。接近（語句）;
             }
        } 趕上（MySQLIntegrityConstraintViolationException五）{
            //重複條目，假設OK

            RES 。的getOutputStream（） 。打印（“ * OK * “）;
        } 趕上（例外五）{
            水庫。 setStatus（500）;

            Ë 。的printStackTrace（）;
        } 終於 {
            BitcoinDatabaseManager 。接近（CONN）;
        }
    }
}
