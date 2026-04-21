import React, { useState } from 'react';

const RegisterForm = ({ onSwitch, onRegister, isSubmitting }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'TRAINEE',
  });
  const handleSubmit = async (e) => {
    e.preventDefault();
    await onRegister({ ...formData, email: formData.email.trim().toLowerCase() });
  };
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  return (
    <div className="form-container">
      <h2 className="form-title">Create Account</h2>
      <p className="form-subtitle">Join us and start your journey.</p>
      
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label className="input-label" htmlFor="firstName">First Name</label>
          <input 
            type="text" 
            id="firstName"
            name="firstName"
            className="input-field" 
            placeholder="Enter your first name"
            value={formData.firstName}
            onChange={handleChange}
            required 
          />
        </div>
        <div className="input-group">
          <label className="input-label" htmlFor="lastName">Last Name</label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            className="input-field"
            placeholder="Enter your last name"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>

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
            placeholder="Create a password" 
            value={formData.password}
            onChange={handleChange}
            required 
          />
        </div>
        <div className="input-group">
          <label className="input-label" htmlFor="role">Role</label>
          <select id="role" name="role" className="input-field" value={formData.role} onChange={handleChange}>
            <option value="TRAINEE">Trainee</option>
            <option value="MANAGER">Manager</option>
          </select>
        </div>
        
        <button type="submit" className="submit-btn" style={{ animationDelay: '0.4s' }} disabled={isSubmitting}>
          {isSubmitting ? 'Creating Account...' : 'Sign Up'}
        </button>
      </form>
      
      <p className="toggle-text">
        Already have an account? 
        <span className="toggle-link" onClick={onSwitch}>Sign In</span>
      </p>
    </div>
  );
};

export default RegisterForm;
