import React from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Navbar from'./components/Navbar';
import PostsTable from "./pages/PostsTable";
import PostDetail from "./pages/postDetail/PostDetail";
import MakingPost from "./pages/postDetail/MakingPost";
import SignIn from "./pages/login/SignIn";
import SignUp from "./pages/login/SignUp";
import SearchTable from "./pages/SearchTable";

function App() {
  return (
      <>
        <Router>
          <Navbar/>
          <Routes>
            <Route exact path='/' element={<PostsTable />}/>
            <Route path='/search/:keyword' element={<SearchTable />}/>
            <Route path='/post/id/:postId' element={<PostDetail/>}/>
            <Route path='/post' element={<MakingPost/>}/>
            <Route path='/login' element={<SignIn/>}/>
            <Route path='/signUp' element={<SignUp/>}/>
          </Routes>
        </Router>
      </>
  );
}

export default App;
