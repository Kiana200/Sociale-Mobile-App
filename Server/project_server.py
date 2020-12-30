import base64

from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import or_, and_
from flask_jwt_extended import create_access_token, JWTManager, jwt_required, get_raw_jwt, decode_token
from flask_bcrypt import Bcrypt
import datetime
import os

app = Flask(__name__)

if 'NAMESPACE' in os.environ and os.environ['NAMESPACE'] == 'heroku':
    db_uri = os.environ['DATABASE_URL']
    debug_flag = False
else:  # when running locally: use sqlite
    db_path = os.path.join(os.path.dirname(__file__), 'app.db')
    db_uri = 'sqlite:///{}'.format(db_path)
    debug_flag = True

app.config['SQLALCHEMY_DATABASE_URI'] = db_uri
db = SQLAlchemy(app)
bcrypt = Bcrypt(app)
app.config["JWT_SECRET_KEY"] = "elg28h93k329gj3nw9fo2n2ngpg2j"
app.config['JWT_BLACKLIST_ENABLED'] = True
app.config['JWT_BLACKLIST_TOKEN_CHECKS'] = ['access']
expires = datetime.timedelta(days=7)
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = expires
jwt = JWTManager(app)

read_by = db.Table('read_by',
                   db.Column('message_id', db.Integer, db.ForeignKey('message.id'), primary_key=True),
                   db.Column('username', db.String(15), db.ForeignKey('user.username'), primary_key=True)
                   )


class Date(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user = db.Column(db.String(15), db.ForeignKey('user.username'))  # The user that booked the Date
    post = db.Column(db.Integer, db.ForeignKey('post.id'))  # Reference to which post the Date belongs to
    date = db.Column(db.String(100), unique=False, nullable=False)

    def to_dict(self):
        return {'id': self.id, 'user': self.user, 'post': self.post, 'date': self.date}


class Blacklist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)
    revoked = db.Column(db.Boolean, nullable=False)
    user = db.Column(db.String(15), nullable=False)

    def __init__(self, jti, user, revoked=False):
        self.jti = jti
        self.revoked = revoked
        self.user = user

    def to_dict(self):
        return {'revoked': self.revoked, 'user': self.user}


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    message = db.Column(db.String(200), unique=False, nullable=False)
    read_by = db.relationship('User', secondary=read_by, lazy='subquery',
                              backref=db.backref('read_messages', lazy=True))
    sender = db.Column(db.String(15), db.ForeignKey('user.username'))
    receiver = db.Column(db.String(15), db.ForeignKey('user.username'))

    def to_dict(self):
        return {"id": self.id,
                "message": self.message,
                "sender": self.sender,
                "receiver": self.receiver,
                "read_by": {"userList": [x.to_dict() for x in self.read_by]}}  # userList used for java bean in Android


class Review(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    review_message = db.Column(db.String(240), unique=False, nullable=False)
    post = db.Column(db.Integer, db.ForeignKey('post.id'))  # Reference to the post of which the review corresponds to
    publisher = db.Column(db.String(15), db.ForeignKey('user.username'))

    def to_dict(self):
        return {"id": self.id,
                "review": self.review_message,
                "post_id": self.post,
                "publisher": self.publisher}


class User(db.Model):
    username = db.Column(db.String(15), unique=True, nullable=False, primary_key=True)
    email = db.Column(db.String(120), unique=True, nullable=False)
    password = db.Column(db.String(200), unique=False, nullable=False)
    reviews = db.relationship("Review", backref='user_reviews', lazy=True)

    def __init__(self, username, password, email):
        self.username = username
        self.password = bcrypt.generate_password_hash(password).decode('utf-8')
        self.email = email

    def to_dict(self):
        return {'username': self.username, 'email': self.email,
                'reviews': {"reviewList": [x.to_dict() for x in self.reviews]}}  # reviewList used for java bean


class Post(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    rubric = db.Column(db.String(30), unique=False, nullable=False)
    size = db.Column(db.String(10), unique=False, nullable=False)
    posted_by = db.Column(db.String(15), db.ForeignKey('user.username'))
    category = db.Column(db.String(120), unique=False, nullable=False)
    reviews = db.relationship("Review", backref='post_reviewed', lazy=True)
    dates = db.relationship('Date', backref='booked_dates', lazy=True)  # All dates when the Post is booked
    photo = db.Column(db.LargeBinary, unique=False, nullable=False)
    description = db.Column(db.String(200), unique=False, nullable=False)
    latitude = db.Column(db.Float, unique=False, nullable=False)
    longitude = db.Column(db.Float, unique=False, nullable=False)

    def to_dict(self):
        return {"id": self.id, "size": self.size, "posted_by": self.posted_by, "category": self.category,
                "reviews": {"reviewList": [x.to_dict() for x in self.reviews]},
                "dateList": [x.to_dict() for x in self.dates],
                "photo": base64.b64encode(self.photo).decode('utf-8'), "description": self.description,
                "rubric": self.rubric, "latitude": str(self.latitude), "longitude": str(self.longitude)}


@jwt.token_in_blacklist_loader
def check_if_token_in_blacklist(decrypted_token):
    jti = decrypted_token['jti']
    token = Blacklist.query.filter_by(jti=jti).first()
    if token.revoked:
        return True
    else:
        return False


@app.route('/', methods=["GET"])
def start():
    return jsonify(start="hello")  # Not used in the app, used to check whether the server is up


@app.route('/create/user', methods=["POST"])
def register():
    if "username" in request.form and "password" in request.form and \
            "email" in request.form:  # Check that all required data has been received
        username = request.form["username"]
        password = request.form["password"]
        email = request.form["email"]
        user = User.query.filter_by(username=username).first()
        if user is None:  # There is no current user with the same username, so we can create new user
            user = User(username, password, email)
            db.session.add(user)
            db.session.commit()
            return jsonify("registered"), 200
        return jsonify('Username already taken'), 409
    else:
        return jsonify("Missing data"), 404


@app.route('/user/login', methods=["POST"])
def login():
    if "username" in request.form and "password" in request.form:  # Check that all required data has been received
        username = request.form["username"]
        password = request.form["password"]
        user = User.query.filter_by(username=username).first()
        if user is not None:  # User exists
            if bcrypt.check_password_hash(user.password, password):  # Correct password
                token = create_access_token(identity=user.username)
                new_token = Blacklist(jti=decode_token(token)['jti'], user=username)
                db.session.add(new_token)
                db.session.commit()
                return jsonify(token=token)
        else:
            return jsonify("User not found!"), 404
    else:
        return jsonify("Missing data"), 404


@app.route('/user/logout', methods=["POST"])
@jwt_required
def log_out():
    jti = get_raw_jwt()['jti']
    blacklist_token = Blacklist.query.filter_by(jti=jti).first()
    blacklist_token.revoked = True  # Revoke the token
    db.session.add(blacklist_token)
    db.session.commit()
    return jsonify('Access token revoked'), 200


@app.route('/create/review', methods=["POST"])
@jwt_required
def write_review():
    if "post_id" in request.form and "review" in request.form and \
            "username" in request.form:  # Check that all required data has been received
        review_message = request.form["review"]
        post_id = int(request.form["post_id"])
        username = request.form["username"]
        post = Post.query.filter_by(id=post_id).first()
        user = User.query.filter_by(username=username).first()
        if post is not None and user is not None and review_message != "" and \
                review_message is not None:  # Check that received data is valid
            review = Review(review_message=review_message, publisher=username, post=post_id)
            db.session.add(review)
            db.session.commit()
            return jsonify("Review created")
        else:
            return jsonify("User or post not found"), 404
    else:
        return jsonify("Missing data"), 404


@app.route('/filter', methods=["POST"])
@jwt_required
def filter_posts():
    if "size" in request.form and "category" in request.form:  # Both size and category chosen
        size_filter = request.form['size']
        category_filter = request.form["category"]
        filtered_posts = Post.query.filter(Post.size.like(size_filter)).filter(Post.category.like(category_filter))
        return jsonify(postList=[x.to_dict() for x in filtered_posts])
    elif "size" in request.form:  # Only size chosen
        size_filter = request.form['size']
        filtered_posts = Post.query.filter_by(size=size_filter)
        return jsonify(postList=[x.to_dict() for x in filtered_posts])
    elif "category" in request.form:  # Only category chosen
        category_filter = request.form["category"]
        filtered_posts = Post.query.filter_by(category=category_filter)
        return jsonify(postList=[x.to_dict() for x in filtered_posts])
    else:
        return jsonify(postList=[x.to_dict() for x in Post.query.all()])  # Otherwise no filters chosen, should
        # display all posts


@app.route('/find/user/<username>', methods=["GET"])
@jwt_required
def find_user(username):
    """Not used in code at the moment, but could be useful for e.g. a search function
    to find a specific user"""
    user = User.query.filter_by(username=username).first()
    if user is not None:
        return jsonify(user.to_dict())
    else:
        return jsonify("User not found"), 404


@app.route('/create/post', methods=["POST"])
@jwt_required
def create_post():
    if "size" in request.form and "category" in request.form and \
            "username" in request.form and "photo" in request.form and \
            "rubric" in request.form and "description" in request.form and \
            "latitude" in request.form and "longitude" in request.form:  # Check that all required data has been
        # received
        rubric = request.form["rubric"]
        size = request.form['size']
        category = request.form["category"]
        username = request.form["username"]
        description = request.form["description"]
        photo = base64.b64decode(request.form['photo'])
        latitude = float(request.form["latitude"])
        longitude = float(request.form["longitude"])
        post = Post(size=size, rubric=rubric, category=category, posted_by=username, photo=photo,
                    description=description, latitude=latitude, longitude=longitude)
        db.session.add(post)
        db.session.commit()
        post_id = post.id
        return jsonify(id=post_id)
    else:
        return jsonify("Missing data"), 404


@app.route('/book', methods=['POST'])
@jwt_required
def book():
    if "date" in request.form and "username" in request.form and "post_id" in request.form:  # Check that all required
        # data has been received
        date_string = request.form['date']
        year = int(date_string[0:4])
        month = int(date_string[5:7])
        day = int(date_string[8:])
        date = datetime.date(year, month, day)  # Could probably be made more efficient, but did not have time to fix
        current_date = datetime.date.today()
        if date >= current_date:  # Check that the date to be booked is not before today's date
            username = request.form["username"]
            post_id = int(request.form["post_id"])
            post = Post.query.filter_by(id=post_id).first()
            all_booked_dates = post.dates
            for booked_date in all_booked_dates:
                if date_string == booked_date.date:
                    return jsonify("Date already booked"), 500
            user = User.query.filter_by(username=username).first()
            if user is not None and post is not None:  # Not booked yet
                new_date = Date(user=user.username, post=post_id, date=date)
                post.dates.append(new_date)
                db.session.add(post)
                db.session.add(new_date)
                db.session.commit()
                return jsonify("Booked")
            else:
                return jsonify("Invalid username or post id"), 400
        else:
            return jsonify("Selected date cannot be before current date."), 400
    else:
        return jsonify("Missing data"), 404


@app.route("/cancel/<post_id>/<date>", methods=["DELETE"])
@jwt_required
def cancel(post_id, date):
    post = Post.query.filter_by(id=post_id).first()
    if post is not None:
        post_dates = post.dates
        for booked_date in post_dates:
            if booked_date.date == date:  # Found the right date, remove date and all references to it.
                post.dates.remove(booked_date)
                Date.query.filter_by(id=booked_date.id).delete()
                db.session.commit()
                return jsonify("Booking cancelled")
        return jsonify("No matching booked date found for post"), 404
    else:
        return jsonify("No post with id " + post_id + " exists."), 404


@app.route("/messages/send", methods=["POST"])
@jwt_required
def send_message():
    if "message" in request.form and "sender" in request.form and "receiver" in request.form:  # Check that all required
        # data has been received
        message = request.form["message"]
        sender = request.form["sender"]
        jti = get_raw_jwt()['jti']
        token = Blacklist.query.filter_by(jti=jti).first()
        if token.user == sender:  # The user that is trying to send a message is the person logged in.
            # This check is not really needed since the Android code always sends the username of the person logged in.
            receiver = request.form["receiver"]
            user_receiver = User.query.filter_by(username=receiver).first()
            if user_receiver is not None:
                message = Message(message=message, sender=sender, receiver=receiver)  # Create new message, i.e. send
                db.session.add(message)
                db.session.commit()
                return jsonify(messageList=[message.to_dict()])
            else:
                return jsonify("No user with username " + receiver + " exists"), 404
    else:
        return jsonify("Missing data"), 404


@app.route('/delete/message/<message_id>', methods=["DELETE"])
@jwt_required
def delete_message(message_id):
    message = Message.query.filter_by(id=message_id).first()
    if message is None:
        return jsonify('Message with id ' + message_id + ' does not exist.'), 404
    else:
        users = User.query.all()
        for user in users:
            for message in user.read_messages:
                if message.id == int(message_id):
                    user.read_messages.remove(message)  # Remove all references to the message
        Message.query.filter_by(id=message_id).delete()
        db.session.commit()
        return jsonify("Message deleted.")


@app.route('/delete/post/<post_id>', methods=["DELETE"])
@jwt_required
def delete_post(post_id):
    print(post_id)
    post = Post.query.filter_by(id=post_id).first()
    if post is None:
        return jsonify('Post with id ' + post_id + ' does not exist.'), 404
    else:
        all_reviews = Review.query.all()
        reviews = [x.to_dict() for x in all_reviews]
        for review in reviews:
            if review["post_id"] == int(post_id):
                post_dict = post.to_dict()
                post_dict["reviews"]["reviewList"].remove(review)  # Remove all reviews from the post
        all_dates = Date.query.all()
        dates = [x.to_dict() for x in all_dates]
        for date in dates:
            if date["post"] == int(post_id):
                date_id = date["id"]
                Date.query.filter_by(id=date_id).delete() # Remove all bookings for post
        Post.query.filter_by(id=post_id).delete()
        db.session.commit()
        return jsonify("Post deleted.")


@app.route('/delete/review/<review_id>', methods=["DELETE"])
@jwt_required
def delete_review(review_id):
    review = Review.query.filter_by(id=review_id).first()
    if review is None:
        return jsonify("Review with id " + review_id + " does not exist."), 404
    else:
        posts = Post.query.all()
        for post in posts:
            post_dict = post.to_dict()
            review_list = post_dict["reviews"]["reviewList"]
            for review in review_list:
                if review["id"] == int(review_id):
                    review_list.remove(review)
                    break  # We assume that a review only appears once in one post
        Review.query.filter_by(id=review_id).delete()
        db.session.commit()
        return jsonify("Review deleted.")


@app.route('/get/all/messages/<user_1>/<user_2>', methods=["GET"])
@jwt_required
def get_all_messages(user_1, user_2):
    """Returns both messages sent by and received by both users, i.e. all
    messages between the two users. Used in the message fragment in the app."""
    all_messages = Message.query.filter(or_(and_(Message.sender.like(user_1), Message.receiver.like(user_2)),
                                            and_(Message.sender.like(user_2),
                                                 Message.receiver.like(user_1)))).order_by(Message.id)
    messages = [x.to_dict() for x in all_messages]
    return jsonify(messageList=messages)


@app.route('/messages/get/all/users/<username>', methods=["GET"])
@jwt_required
def get_all_users_for_message(username):
    """Gets all the users the person with the username has either sent
    messages to or received messages from. Used to create the message menu in the app"""
    all_received_messages = Message.query.filter_by(receiver=username)  # Could be done as in get_all_messages in one
    # query, but did not have time to change.
    all_sent_messages = Message.query.filter_by(sender=username)
    received_messages = [x.to_dict() for x in all_received_messages]
    sent_messages = [x.to_dict() for x in all_sent_messages]
    all_users = []  # List to put users in
    for received_message in received_messages:
        user_to_append = User.query.filter_by(username=received_message["sender"]).first()  # Append senders
        if user_to_append not in all_users and user_to_append is not None:
            all_users.append(user_to_append)
    for sent_message in sent_messages:
        user_to_append = User.query.filter_by(username=sent_message["receiver"]).first()  # Append receivers
        if user_to_append not in all_users and user_to_append is not None:
            all_users.append(user_to_append)
    return jsonify(userList=[x.to_dict() for x in all_users])


@app.route('/get/all/posts/<username>', methods=["GET"])
@jwt_required
def get_all_posts_for_user(username):
    """Returns all the posts created by the given user. Used in the profile fragment to show
    posts"""
    all_posts = Post.query.filter_by(posted_by=username)
    posts = [x.to_dict() for x in all_posts]
    return jsonify(postList=posts)


@app.route('/get/all/posts', methods=["GET"])
@jwt_required
def get_all_posts():
    """Returns all created posts. Often used on the start page."""
    all_posts = Post.query.all()
    posts = [x.to_dict() for x in all_posts]
    return jsonify(postList=posts)


@app.route('/get/all/sizes', methods=["GET"])
@jwt_required
def get_all_sizes():
    """Returns all sizes, used when filtering posts."""
    all_posts = Post.query.all()
    posts = [x.to_dict() for x in all_posts]
    all_sizes = []
    for post in posts:
        if post["size"] not in all_sizes:  # We do not want doubles
            all_sizes.append(post["size"])
    return jsonify(filterList=all_sizes)


@app.route('/get/all/categories', methods=["GET"])
@jwt_required
def get_all_categories():
    """Returns all categories, used when filtering posts."""
    all_posts = Post.query.all()
    posts = [x.to_dict() for x in all_posts]
    all_categories = []
    for post in posts:
        if post["category"] not in all_categories:  # We do not want doubles
            all_categories.append(post["category"])
    return jsonify(filterList=all_categories)


@app.route('/get/all/users', methods=['GET'])
@jwt_required
def get_all_users():
    """Returns all users created. Not used at the moment."""
    all_users = User.query.all()
    users = [x.to_dict() for x in all_users]
    return jsonify(userList=users)


@app.route('/get/post/<post_id>', methods=["GET"])
def get_post(post_id):
    """Returns the Post with the given id. Used when a user has clicked on a post
    on the start page in the app."""
    post = Post.query.filter_by(id=post_id).first()
    if post is None:
        return jsonify("No post with id " + str(post_id)), 404
    else:
        return jsonify(post.to_dict())


@app.route('/get/review/<review_id>', methods=["GET"])
@jwt_required
def get_review(review_id):
    """Returns the Review with the given id. Not currently used."""
    review = Review.query.filter_by(id=review_id).first()
    if review is None:
        return jsonify("No review with id " + str(review_id)), 404
    else:
        return jsonify(review.to_dict())


@app.route('/get/all/reviews/<post_id>', methods=["GET"])
@jwt_required
def get_all_reviews(post_id):
    """Returns all the reviews for a Post, used when clicking on "Reviews" in a Post."""
    all_reviews = Review.query.filter_by(post=post_id)
    reviews = [x.to_dict() for x in all_reviews]
    return jsonify(reviewList=reviews)


@app.route('/get/message/<message_id>', methods=["GET"])
@jwt_required
def get_message(message_id):
    """Returns the Message with the given id. Not used at the moment."""
    message = Message.query.filter_by(id=message_id).first()
    if message is None:
        return jsonify('Message with id ' + message_id + ' does not exist.'), 404
    else:
        return jsonify(message.to_dict())


@app.route('/mark/<message_id>/read/<username>', methods=["POST"])
@jwt_required
def mark_as_read(message_id, username):
    """Marks a message as read by the given user. Not used at the moment, but would be useful in further
    app development to be able to see if the other person has read the message."""
    message = Message.query.filter_by(id=message_id).first()
    if message is None:
        return jsonify('Message with id ' + message_id + ' does not exist.'), 404
    else:
        user = User.query.filter_by(username=username).first()
        if user is None:
            return jsonify("User " + username + " does not exist"), 404
        else:
            message.read_by.append(user)
            db.session.commit()
            user.read_messages.append(message)
            db.session.commit()
            return jsonify("Marked as read.")


@app.route('/messages/unread/<username>', methods=["GET"])
@jwt_required
def get_unread_messages(username):
    """Return all messages which are not marked as read by the given user. Not currently used,
    but could replace get_all_messages if reimplemented to only return unread messages in a specific conversation
    between two users."""
    unread_messages = Message.query.all()  # Start with all, then remove read messages
    for message in unread_messages:
        for user in message.read_by:
            if username == user.username:
                unread_messages.remove(message)
    return jsonify(messageList=[x.to_dict() for x in unread_messages])


@app.route('/change/username', methods=["POST"])
@jwt_required
def change_username():
    """Changes a user's username. Needs to change all occurrences in foreign keys as well.
    We did not have time to implement this, so we could not fully implement this function in settings."""
    if "oldUsername" in request.form and "newUsername" in request.form:  # Check that all required data has been
        # received
        old_username = request.form["oldUsername"]
        user = User.query.filter_by(username=old_username).first()
        new_username = request.form["newUsername"]
        user_with_username = User.query.filter_by(username=new_username).first()
        if user_with_username is None:  # Username not taken
            user.username = new_username
            db.session.commit()
            return jsonify("Username changed")
        else:
            return jsonify("Username taken"), 409
    else:
        return jsonify("Missing data"), 404


@app.route('/change/email', methods=["POST"])
@jwt_required
def change_email():
    """Changes a user's email."""
    if "username" in request.form and "newEmail" in request.form:  # Check that all required data has been received
        username = request.form["username"]
        user = User.query.filter_by(username=username).first()
        new_email = request.form["newEmail"]
        user_with_email = User.query.filter_by(email=new_email).first()
        if user_with_email is None:  # Email not taken
            user.email = new_email
            db.session.commit()
            return jsonify("Email changed")
        else:
            return jsonify("Email taken"), 409
    else:
        return jsonify("Missing data"), 404


@app.route('/get/all/reviews/user/<username>', methods=["GET"])
@jwt_required
def get_all_reviews_user(username):
    """Returns all reviews written by the given user. Used in the profile fragment to show reviews."""
    all_reviews = Review.query.filter_by(publisher=username)
    reviews = [x.to_dict() for x in all_reviews]
    return jsonify(reviewList=reviews)


@app.route('/get/all/posts/user/<username>', methods=["GET"])
@jwt_required
def get_all_posts_user(username):
    """Returns all posts created by the given user. Used in the profile fragment to show posts."""
    all_posts = Post.query.filter_by(posted_by=username)
    posts = [x.to_dict() for x in all_posts]
    return jsonify(postList=posts)


@app.route('/get/all/bookings/<username>', methods=["GET"])
@jwt_required
def get_all_bookings(username):
    """Returns all bookings by the given user. Used in the profile fragment to show bookings."""
    all_dates = Date.query.filter_by(user=username)
    dates = [x.to_dict() for x in all_dates]
    return jsonify(dateList=dates)


if __name__ == '__main__':
    db.drop_all()
    db.create_all()
    app.debug = True
    app.run(port=9089)
