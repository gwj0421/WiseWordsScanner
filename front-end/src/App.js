import React, {useState} from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Navbar from'./components/Navbar';
import PostsTable from "./pages/PostsTable";
import PostDetail from "./pages/postDetail/PostDetail";
import MakingPost from "./pages/postDetail/MakingPost";
import SignIn from "./pages/login/SignIn";
import SignUp from "./pages/login/SignUp";
import SearchTable from "./pages/SearchTable";

function App() {
    const [loggedIn, setLoggedIn] = useState(false);
    return (
      <>
        <Router>
          <Navbar loggedIn={loggedIn} setLoggedIn={setLoggedIn}/>
          <Routes>
            <Route exact path='/' element={<PostsTable setLoggedIn={setLoggedIn}/>}/>
            <Route path='/search/:keyword' element={<SearchTable setLoggedIn={setLoggedIn}/>}/>
            <Route path='/post/id/:postId' element={<PostDetail setLoggedIn={setLoggedIn}/>}/>
            <Route path='/post' element={<MakingPost setLoggedIn={setLoggedIn}/>}/>
            <Route path='/login' element={<SignIn />}/>
            <Route path='/signUp' element={<SignUp />}/>
          </Routes>
        </Router>
      </>
  );
}

export default App;
