# import statements
from flask import *
from flask_bcrypt import Bcrypt
from flask_mysqldb import MySQL
import MySQLdb.cursors
# initialization
app = Flask(__name__)
bcrypt = Bcrypt()

# config
app.config['MYSQL_HOST'] = 'remotemysql.com'
app.config['MYSQL_USER'] = 'F5shCxBMxe'
app.config['MYSQL_PASSWORD'] = 'g1rMHVIhIq'
app.config['MYSQL_DB'] = 'F5shCxBMxe'

mysql = MySQL(app)

# Api's


@app.route("/")
def home():
    return render_template('login.html')


@app.route("/login")
def login():
    return render_template('login.html')


@app.route("/signup", methods=["POST", "GET"])
def signup():
    if(request.method == "POST"):

        # get the data from the form
        name = request.form['name']
        email = request.form['email']
        password = request.form['password']

        # hash the password
        pw_hash = bcrypt.generate_password_hash(password)

        # initialize the cursor
        signup_cursor = mysql.connection.cursor()

        # check whether user already exists
        user_result = signup_cursor.execute(
            "SELECT * FROM USERS WHERE user_email=%s", [email]
        )
        if(user_result > 0):
            signup_cursor.close()
            return render_template('signup.html', error=True)
        else:
            # execute the query
            signup_cursor.execute(
                'INSERT INTO USERS(user_name,user_email,user_password) VALUES(%s,%s,%s)', (
                    name, email, str(pw_hash)
                )
            )

            mysql.connection.commit()
            signup_cursor.close()
            return redirect(url_for('home'))

    return render_template('signup.html', error=False)


# main
if __name__ == "__main__":
    app.run()
