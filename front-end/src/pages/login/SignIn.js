import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {createTheme, ThemeProvider} from '@mui/material/styles';
import {httpClientForCredentials} from "../../index";
import {useNavigate} from 'react-router-dom';

function Copyright(props) {
    return (<Typography variant="body2" color="text.secondary" align="center" {...props}>
        {'Copyright © '}
        <Link color="inherit" href="https://mui.com/">
            Your Website
        </Link>{' '}
        {new Date().getFullYear()}
        {'.'}
    </Typography>);
}

// TODO remove, this demo shouldn't need to reset the theme.

const defaultTheme = createTheme();

export default function SignIn() {
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();

        const formData = new FormData(event.currentTarget);
        const data = {
            userId: formData.get('userId'), password: formData.get('password')
        }
        try {
            const response = await httpClientForCredentials.post('/api/user/login', data, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 200) {
                // window.location.replace('/');
                navigate('/',{replace:true})
            }
        } catch (e) {
            console.error(e);
        }

    }
    return (<ThemeProvider theme={defaultTheme}>
        <Container component="main" maxWidth="xs">
            <CssBaseline/>
            <Box
                sx={{
                    marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center',
                }}
            >
                <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                    <LockOutlinedIcon/>
                </Avatar>
                <Typography component="h1" variant="h5">
                    Sign in
                </Typography>
                <Box component="form" onSubmit={handleSubmit} noValidate sx={{mt: 1}}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="userId"
                        label="userId"
                        name="userId"
                        autoComplete="userId"
                        autoFocus
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                    />
                    <FormControlLabel
                        control={<Checkbox value="remember" color="primary"/>}
                        label="Remember me"
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{mt: 3, mb: 2}}
                    >
                        Sign In
                    </Button>
                    <Grid container>
                        <Grid item xs>
                            <Link href="#" variant="body2">
                                Forgot password?
                            </Link>
                        </Grid>
                        <Grid item>
                            <Link href="/signUp" variant="body2">
                                {"Don't have an account? Sign Up"}
                            </Link>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
            <Copyright sx={{mt: 8, mb: 4}}/>
        </Container>
    </ThemeProvider>);
};