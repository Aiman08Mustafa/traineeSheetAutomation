import React, { useState } from 'react';

const LoginForm = ({ onSwitch, onLogin, isSubmitting }) => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const handleSubmit = async (e) => {
    e.preventDefault();
    await onLogin({ ...formData, email: formData.email.trim().toLowerCase() });
  };
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <div className="form-container">
      <h2 className="form-title">Welcome Back</h2>
      <p className="form-subtitle">Please enter your details to sign in.</p>
      
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label className="input-label" htmlFor="email">Email Address</label>
          <input 
            type="email" 
            id="email"
            name="email"
            className="input-field" 
            placeholder="Enter your email" 
            value={formData.email}
            onChange={handleChange}
            required 
          />
        </div>
        
        <div className="input-group">
          <label className="input-label" htmlFor="password">Password</label>
          <input 
            type="password" 
            id="password"
            name="password"
            className="input-field" 
            placeholder="Enter your password" 
            value={formData.password}
            onChange={handleChange}
            required 
          />
        </div>
        
        <button type="submit" className="submit-btn" style={{ animationDelay: '0.3s' }} disabled={isSubmitting}>
          {isSubmitting ? 'Signing In...' : 'Sign In'}
        </button>
      </form>
      
      <p className="toggle-text">
        Don't have an account? 
        <span className="toggle-link" onClick={onSwitch}>Sign Up</span>
      </p>
    </div>
  );
};

export default LoginForm;
