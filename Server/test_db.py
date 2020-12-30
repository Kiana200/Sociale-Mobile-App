import os
import tempfile
import pytest
from project_server import app, db
import datetime
import base64


@pytest.fixture
def client():
    db_fd, app.config['DATABASE_FILE_PATH'] = tempfile.mkstemp()
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///' + app.config['DATABASE_FILE_PATH']
    app.config['TESTING'] = True

    client = app.test_client()

    with app.app_context():
        db.drop_all()
        db.create_all()

    yield client

    os.close(db_fd)
    os.unlink(app.config['DATABASE_FILE_PATH'])


def test_start(client):
    r = client.get("/")
    assert r.status_code == 200
    assert r.json["start"] == "hello"


def test_register(client):
    r = client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    assert r.status_code == 200
    r = client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    assert r.status_code == 409
    r = client.post("/create/user", data={"username": "Kiana", "email": "test@test.se"})
    assert r.status_code == 404


def test_login(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    assert r.status_code == 200
    r = client.post("/user/login", data={"username": "Julia", "password": "tddd80"})
    assert r.status_code == 404
    r = client.post("/user/login", data={"password": "tddd80"})
    assert r.status_code == 404


def test_logout(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/user/logout', headers=header)
    r = client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    assert r.status_code == 401


def test_write_review(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": "1"}, headers=header)
    assert r.status_code == 404
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": "1"}, headers=header)
    assert r.status_code == 200
    r = client.post("/create/review", data={}, headers=header)
    assert r.status_code == 404


def test_filter_posts(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                      "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "XL",
                                      "category": "Pants", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                      "category": "Shirt", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.post("/filter", data={"size": "S"}, headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 2
    r = client.post("/filter", data={"size": "S", "category": "Dress"}, headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 1
    r = client.post("/filter", headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 3
    r = client.post("/filter", data={"category": "Shirt"}, headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 1


def test_find_user(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test1@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/find/user/Julia", headers=header)
    assert r.status_code == 200
    assert r.json["username"] == "Julia"
    r = client.get("/find/user/Anonymous", headers=header)
    assert r.status_code == 404


def test_create_post(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    assert r.status_code == 200
    r = client.get("/get/all/posts/Kiana", headers=header)
    assert len(r.json) == 1
    r = client.post("/create/post", headers=header)
    assert r.status_code == 404


def test_book(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    date = datetime.date(2021, 5, 25)
    r = client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    assert r.status_code == 200
    r = client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    assert r.status_code == 500
    date = datetime.date(2021, 5, 26)
    r = client.post("/book", data={"username": "Julia", "post_id": 1, "date": date}, headers=header)
    assert r.status_code == 400
    r = client.post("/book", headers=header)
    assert r.status_code == 404
    date = datetime.date(2019, 6, 25)
    r = client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    assert r.status_code == 400


def test_cancel(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    date = datetime.date(2021, 5, 25)
    client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    r = client.delete("/cancel/1/" + date.strftime("%Y-%m-%d"), headers=header)
    assert r.status_code == 200
    date = datetime.date(2021, 6, 25)
    r = client.delete("/cancel/1/" + date.strftime("%Y-%m-%d"), headers=header)
    assert r.status_code == 404
    r = client.delete("/cancel/2/" + date.strftime("%Y-%m-%d"), headers=header)
    assert r.status_code == 404


def test_send_message(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    user_id = r.json["messageList"][0]["id"]
    assert user_id == 1
    r = client.post('/messages/send', data={}, headers=header)
    assert r.status_code == 404
    r = client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Anonymous'}, headers=header)
    assert r.status_code == 404


def test_delete_message(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.get("/messages/unread/Julia", headers=header)
    assert len(r.json["messageList"]) == 1
    client.post("/mark/1/read/Julia", headers=header)
    r = client.get("/messages/unread/Julia", headers=header)
    assert len(r.json["messageList"]) == 0
    r = client.delete('/delete/message/1', headers=header)
    answer = r.data.decode(encoding='utf-8')
    assert answer == '"Message deleted."\n'
    r = client.get('/get/all/messages/Kiana/Julia', headers=header)
    assert len(r.json["messageList"]) == 0
    r = client.delete('/delete/message/1', headers=header)
    assert r.status_code == 404


def test_delete_post(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
    date = datetime.date(2020, 8, 25)
    client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    r = client.delete('/delete/post/1', headers=header)
    assert r.status_code == 200
    r = client.delete('/delete/post/2', headers=header)
    assert r.status_code == 404
    r = client.get('/get/all/posts/Kiana', headers=header)
    assert len(r.json["postList"]) == 0


def test_delete_review(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
    r = client.delete('/delete/review/1', headers=header)
    assert r.status_code == 200
    r = client.get('get/all/reviews/1', headers=header)
    assert len(r.json["reviewList"]) == 0
    r = client.get('/get/all/posts/Kiana', headers=header)
    assert r.json["postList"][0]['reviews']["reviewList"] == []
    r = client.delete('/delete/review/1', headers=header)
    assert r.status_code == 404


def test_get_all_messages(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.get('/get/all/messages/Kiana/Julia', headers=header)
    message = r.json["messageList"][0]["message"]
    assert message == "hej"
    client.post("/messages/send", data={"message": "DU", 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.get('/get/all/messages/Kiana/Julia', headers=header)
    assert len(r.json["messageList"]) == 2


def test_get_all_users_for_message(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    client.post("/create/user", data={"username": "Testbert", "password": "tddd80", "email": "test3@test.se"})
    r = client.post("/user/login", data={"username": "Testbert", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Testbert', 'receiver': 'Kiana'}, headers=header)
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post('/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post('/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.get('/messages/get/all/users/Kiana', headers=header)
    assert r.status_code == 200
    assert len(r.json["userList"]) == 2


def test_get_all_posts_for_user(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/get/all/posts/Kiana", headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 0


def test_get_all_posts(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/get/all/posts", headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 0


def test_get_all_sizes(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/get/all/sizes", headers=header)
    assert r.status_code == 200
    assert len(r.json["filterList"]) == 0
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.get("/get/all/sizes", headers=header)
    assert r.status_code == 200
    assert len(r.json["filterList"]) == 1


def test_get_all_categories(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/get/all/categories", headers=header)
    assert r.status_code == 200
    assert len(r.json["filterList"]) == 0
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.get("/get/all/categories", headers=header)
    print(r.json)

    assert r.status_code == 200
    assert len(r.json["filterList"]) == 1


def test_get_all_users(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.get("/get/all/users", headers=header)
    assert r.status_code == 200
    assert len(r.json["userList"]) == 2


def test_get_post(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.get("/get/post/1", headers=header)
    assert r.json['id'] == 1
    assert r.status_code == 200
    r = client.get("/get/post/2", headers=header)
    assert r.status_code == 404


def test_get_review(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
    r = client.get("get/review/1", headers=header)
    assert r.json["review"] == "Mycket bra"
    assert r.status_code == 200
    r = client.get("get/review/2", headers=header)
    assert r.status_code == 404


def test_get_all_reviews(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                          "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                          "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
    r = client.get('get/all/reviews/1', headers=header)
    assert r.status_code == 200
    assert len(r.json["reviewList"]) == 1


def test_get_message(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post('/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.get('/get/message/1', headers=header)
    message_id = r.json["id"]
    message = r.json["message"]
    assert message_id == 1
    assert message == "hej"
    r = client.get('/get/message/3', headers=header)
    assert r.status_code == 404


def test_marked_as_read(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post('/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    r = client.post("/mark/2/read/Julia", headers=header)
    assert r.status_code == 200
    r = client.get('/get/message/2', headers=header)
    user = r.json['read_by']["userList"][0]
    assert user['username'] == 'Julia'
    r = client.post("/mark/2/read/Anonymous", headers=header)
    assert r.status_code == 404
    r = client.post("/mark/3/read/Julia", headers=header)
    assert r.status_code == 404


def test_get_unread_messages(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post('/messages/send', data={'message': 'DU', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    client.post("/mark/2/read/Julia", headers=header)
    r = client.get("/messages/unread/Julia", headers=header)
    assert len(r.json["messageList"]) == 1
    assert r.status_code == 200


def test_token(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token + "12345"}
    r = client.post('/messages/send', data={'message': 'hej', 'sender': 'Kiana', 'receiver': 'Julia'}, headers=header)
    assert r.status_code == 422


def test_change_username(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.post("/change/username", data={}, headers=header)
    assert r.status_code == 404
    r = client.post("/change/username", data={"oldUsername": "Kiana", "newUsername": "Julia"}, headers=header)
    assert r.status_code == 409
    r = client.post("/change/username", data={"oldUsername": "Kiana", "newUsername": "Kiana2"}, headers=header)
    assert r.status_code == 200
    r = client.get("/find/user/Kiana2", headers=header)
    assert r.status_code == 200
    r = client.get("/find/user/Kiana", headers=header)
    assert r.status_code == 404


def test_change_email(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    client.post("/create/user", data={"username": "Julia", "password": "tddd80", "email": "test2@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    r = client.post("/change/email", data={}, headers=header)
    assert r.status_code == 404
    r = client.post("/change/email", data={"username": "Kiana", "newEmail": "test2@test.se"}, headers=header)
    assert r.status_code == 409
    r = client.post("/change/email", data={"username": "Kiana", "newEmail": "kiana@gmail.com"}, headers=header)
    assert r.status_code == 200
    r = client.get("/find/user/Kiana", headers=header)
    assert r.json["email"] == "kiana@gmail.com"


def test_get_all_reviews_user(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                      "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    client.post("/create/review", data={"username": "Kiana", "review": "Mycket bra", "post_id": 1}, headers=header)
    r = client.get('get/all/reviews/user/Kiana', headers=header)
    assert r.status_code == 200
    assert len(r.json["reviewList"]) == 1

def test_get_all_posts_user(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                      "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    r = client.get('get/all/posts/user/Kiana', headers=header)
    assert r.status_code == 200
    assert len(r.json["postList"]) == 1


def test_get_all_bookings(client):
    client.post("/create/user", data={"username": "Kiana", "password": "tddd80", "email": "test@test.se"})
    r = client.post("/user/login", data={"username": "Kiana", "password": "tddd80"})
    token = r.json['token']
    header = {"Authorization": "Bearer " + token}
    client.post("/create/post", data={"rubric": "Test", "description": "test", "username": "Kiana", "size": "S",
                                      "category": "Dress", "photo": base64.b64encode(b'asdsdas'),
                                      "latitude": "200.0123235424", "longitude": "200.0123235424"}, headers=header)
    date = datetime.date(2021, 5, 25)
    client.post("/book", data={"username": "Kiana", "post_id": 1, "date": date}, headers=header)
    r = client.get('get/all/bookings/Kiana', headers=header)
    assert r.status_code == 200
    assert len(r.json["dateList"]) == 1

