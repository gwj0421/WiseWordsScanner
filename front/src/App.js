import React from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Navbar from'./components/Navbar';
import PostsTable from "./pages/PostsTable";
import PostDetail from "./pages/postDetail/PostDetail";

function App() {
    return (
        <>
            <Router>
                <Navbar/>
                <Routes>
                    <Route exact path='/' element={<PostsTable />}/>
                    <Route path='/post/id/:postId' element={<PostDetail/>}/>
                    <Route path='/post/' element={<MakingPost/>}/>
                </Routes>
            </Router>
        </>
    );
}

export default App;
